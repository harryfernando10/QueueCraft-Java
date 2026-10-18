package com.queuevanta.engine;

import com.queuevanta.model.CounterSnapshot;
import com.queuevanta.model.Customer;
import com.queuevanta.model.CustomerStatus;
import com.queuevanta.model.ServiceCounter;
import com.queuevanta.model.ServiceType;
import com.queuevanta.service.SimulationException;
import com.queuevanta.service.SimulationStats;
import com.queuevanta.strategy.AssignmentStrategy;
import com.queuevanta.strategy.ShortestQueueStrategy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Synchronized in-memory state machine for one Queuevanta simulation. */
public final class SimulationEngine {
    private static final int MAX_EVENT_HISTORY = 250;

    private final Map<String, ServiceCounter> counters = new LinkedHashMap<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Customer> completed = new ArrayList<>();
    private final List<String> events = new ArrayList<>();
    private final AssignmentStrategy strategy;
    private long nextId = 1;
    private long tick;
    private int maxQueue;
    private boolean paused = true;

    /** Creates an engine that uses shortest-load assignment. */
    public SimulationEngine() {
        this(new ShortestQueueStrategy());
    }

    /** Creates an engine with the provided counter assignment policy. */
    public SimulationEngine(AssignmentStrategy strategy) {
        this.strategy = strategy;
    }

    /** Adds a counter whose name is unique, ignoring case. */
    public synchronized void addCounter(ServiceCounter counter) throws SimulationException {
        if (counters.keySet().stream().anyMatch(name -> name.equalsIgnoreCase(counter.getName()))) {
            throw new SimulationException("Counter name already exists.");
        }
        counters.put(counter.getName(), counter);
        log("Counter added: " + counter.getName());
    }

    /** Removes an idle counter. Customers are never orphaned by removing a loaded counter. */
    public synchronized void removeCounter(String name) throws SimulationException {
        ServiceCounter counter = counters.get(name);
        if (counter == null) {
            throw new SimulationException("Counter not found.");
        }
        if (counter.getLoad() > 0) {
            throw new SimulationException("Counter still has customers.");
        }
        counters.remove(name);
        log("Counter removed: " + name);
    }

    /** Changes whether a counter can accept customers and advance its waiting line. */
    public synchronized void setCounterEnabled(String name, boolean enabled)
            throws SimulationException {
        ServiceCounter counter = counters.get(name);
        if (counter == null) {
            throw new SimulationException("Counter not found.");
        }
        counter.setEnabled(enabled);
        log("Counter " + (enabled ? "enabled: " : "disabled: ") + name);
    }

    /** Generates one customer and assigns it immediately to an eligible counter. */
    public synchronized Customer generate(ServiceType serviceType) throws SimulationException {
        Customer customer = new Customer(nextId, "Guest " + nextId, serviceType, tick);
        ServiceCounter counter = strategy.chooseCounter(customer, new ArrayList<>(counters.values()));
        if (counter == null) {
            throw new SimulationException("No enabled counter serves " + serviceType + ".");
        }
        nextId++;
        customers.add(customer);
        counter.enqueue(customer);
        maxQueue = Math.max(maxQueue, counter.getLoad());
        log(customer.getDisplayName() + " joined " + counter.getName());
        return customer;
    }

    /** Generates a batch of 1–100 customers for the same service. */
    public synchronized void generateBatch(ServiceType serviceType, int count)
            throws SimulationException {
        if (count < 1 || count > 100) {
            throw new SimulationException("Batch size must be 1–100.");
        }
        for (int index = 0; index < count; index++) {
            generate(serviceType);
        }
    }

    /** Pauses or resumes tick progression while retaining customer and counter state. */
    public synchronized void setPaused(boolean paused) {
        this.paused = paused;
        log(paused ? "Simulation paused" : "Simulation resumed");
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    /** Advances every counter by one tick if the simulation is running. */
    public synchronized void step() {
        if (paused) {
            return;
        }
        tick++;
        for (ServiceCounter counter : counters.values()) {
            Customer finished = counter.advance();
            if (finished != null) {
                finished.complete(tick);
                completed.add(finished);
                log(finished.getDisplayName() + " completed at " + counter.getName());
            }
            if (counter.getCurrentCustomer() == null && counter.isEnabled()) {
                startNextCustomer(counter);
            }
        }
    }

    /** Clears customers, events, and metrics while keeping counter configuration. */
    public synchronized void reset() {
        tick = 0;
        nextId = 1;
        maxQueue = 0;
        customers.clear();
        completed.clear();
        for (ServiceCounter counter : counters.values()) {
            counter.clear();
        }
        paused = true;
        events.clear();
        log("Simulation reset");
    }

    /** Returns a statistics snapshot for the current simulation state. */
    public synchronized SimulationStats stats() {
        int waiting = 0;
        int serving = 0;
        for (Customer customer : customers) {
            if (customer.getStatus() == CustomerStatus.WAITING) {
                waiting++;
            } else if (customer.getStatus() == CustomerStatus.BEING_SERVED) {
                serving++;
            }
        }
        return new SimulationStats(
                customers.size(),
                waiting,
                serving,
                completed.size(),
                completed.stream().mapToLong(Customer::getWaitingTime).average().orElse(0),
                completed.stream().mapToLong(Customer::getServiceDuration).average().orElse(0),
                maxQueue);
    }

    public synchronized List<ServiceCounter> getCounters() {
        return List.copyOf(counters.values());
    }

    /** Copies counter values while holding the engine lock for safe UI rendering. */
    public synchronized List<CounterSnapshot> getCounterSnapshots() {
        return counters.values().stream()
                .map(
                        counter ->
                                new CounterSnapshot(
                                        counter.getName(),
                                        counter.getServiceType(),
                                        counter.getServiceTicks(),
                                        counter.isEnabled(),
                                        customerDescription(counter.getCurrentCustomer()),
                                        counter.getRemainingTicks(),
                                        counter.getWaiting().stream()
                                                .map(SimulationEngine::customerDescription)
                                                .toList(),
                                        counter.getCompletedCount()))
                .toList();
    }

    public synchronized List<Customer> getCustomers() {
        return List.copyOf(customers);
    }

    public synchronized List<Customer> getCompleted() {
        return List.copyOf(completed);
    }

    public synchronized List<String> getEvents() {
        return List.copyOf(events);
    }

    public synchronized long getTick() {
        return tick;
    }

    private void startNextCustomer(ServiceCounter counter) {
        Customer next = counter.poll();
        if (next == null) {
            return;
        }
        next.startService(tick);
        counter.start(next);
        log(next.getDisplayName() + " started at " + counter.getName());
    }

    private void log(String event) {
        events.add("t=" + tick + "  " + event);
        if (events.size() > MAX_EVENT_HISTORY) {
            events.remove(0);
        }
    }

    private static String customerDescription(Customer customer) {
        if (customer == null) {
            return null;
        }
        return customer.getDisplayName() + " · " + customer.getRequestedService().getLabel();
    }
}
