package com.queuecraft.engine;

import com.queuecraft.model.CounterSnapshot;
import com.queuecraft.model.Customer;
import com.queuecraft.model.CustomerStatus;
import com.queuecraft.model.ServiceCounter;
import com.queuecraft.model.ServiceType;
import com.queuecraft.service.SimulationException;
import com.queuecraft.service.SimulationStats;
import com.queuecraft.strategy.AssignmentStrategy;
import com.queuecraft.strategy.ShortestQueueStrategy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SimulationEngine {
  private final Map<String, ServiceCounter> counters = new LinkedHashMap<>();
  private final List<Customer> customers = new ArrayList<>(), completed = new ArrayList<>();
  private final List<String> events = new ArrayList<>();
  private final AssignmentStrategy strategy;
  private long nextId = 1, tick;
  private int maxQueue;
  private boolean paused = true;

  public SimulationEngine() {
    this(new ShortestQueueStrategy());
  }

  public SimulationEngine(AssignmentStrategy s) {
    strategy = s;
  }

  public synchronized void addCounter(ServiceCounter c) throws SimulationException {
    if (counters.keySet().stream().anyMatch(n -> n.equalsIgnoreCase(c.getName())))
      throw new SimulationException("Counter name already exists.");
    counters.put(c.getName(), c);
    log("Counter added: " + c.getName());
  }

  public synchronized void removeCounter(String name) throws SimulationException {
    ServiceCounter c = counters.get(name);
    if (c == null) throw new SimulationException("Counter not found.");
    if (c.getLoad() > 0) throw new SimulationException("Counter still has customers.");
    counters.remove(name);
    log("Counter removed: " + name);
  }

  public synchronized void setCounterEnabled(String name, boolean on) throws SimulationException {
    ServiceCounter c = counters.get(name);
    if (c == null) throw new SimulationException("Counter not found.");
    c.setEnabled(on);
    log("Counter " + (on ? "enabled: " : "disabled: ") + name);
  }

  public synchronized Customer generate(ServiceType type) throws SimulationException {
    Customer c = new Customer(nextId, "Guest " + nextId, type, tick);
    ServiceCounter counter = strategy.chooseCounter(c, new ArrayList<>(counters.values()));
    if (counter == null) throw new SimulationException("No enabled counter serves " + type + ".");
    nextId++;
    customers.add(c);
    counter.enqueue(c);
    maxQueue = Math.max(maxQueue, counter.getLoad());
    log(c.getDisplayName() + " joined " + counter.getName());
    return c;
  }

  public synchronized void generateBatch(ServiceType t, int count) throws SimulationException {
    if (count < 1 || count > 100) throw new SimulationException("Batch size must be 1–100.");
    for (int i = 0; i < count; i++) generate(t);
  }

  public synchronized void setPaused(boolean value) {
    paused = value;
    log(value ? "Simulation paused" : "Simulation resumed");
  }

  public synchronized boolean isPaused() {
    return paused;
  }

  public synchronized void step() {
    if (paused) return;
    tick++;
    for (ServiceCounter c : counters.values()) {
      Customer done = c.advance();
      if (done != null) {
        done.complete(tick);
        completed.add(done);
        log(done.getDisplayName() + " completed at " + c.getName());
      }
      if (c.getCurrentCustomer() == null && c.isEnabled()) {
        Customer next = c.poll();
        if (next != null) {
          next.startService(tick);
          c.start(next);
          log(next.getDisplayName() + " started at " + c.getName());
        }
      }
    }
  }

  public synchronized void reset() {
    tick = 0;
    nextId = 1;
    maxQueue = 0;
    customers.clear();
    completed.clear();
    for (ServiceCounter c : counters.values()) c.clear();
    paused = true;
    events.clear();
    log("Simulation reset");
  }

  public synchronized SimulationStats stats() {
    int w = 0, s = 0;
    for (Customer c : customers) {
      if (c.getStatus() == CustomerStatus.WAITING) w++;
      else if (c.getStatus() == CustomerStatus.BEING_SERVED) s++;
    }
    return new SimulationStats(
        customers.size(),
        w,
        s,
        completed.size(),
        completed.stream().mapToLong(Customer::getWaitingTime).average().orElse(0),
        completed.stream().mapToLong(Customer::getServiceDuration).average().orElse(0),
        maxQueue);
  }

  public synchronized List<ServiceCounter> getCounters() {
    return List.copyOf(counters.values());
  }

  /** Returns detached counter data safe to render after this method releases the engine lock. */
  public synchronized List<CounterSnapshot> getCounterSnapshots() {
    return counters.values().stream()
        .map(
            counter ->
                new CounterSnapshot(
                    counter.getName(),
                    counter.getServiceType(),
                    counter.getServiceTicks(),
                    counter.isEnabled(),
                    counter.getCurrentCustomer() == null
                        ? null
                        : counter.getCurrentCustomer().getDisplayName(),
                    counter.getRemainingTicks(),
                    counter.getWaiting().stream().map(Customer::getDisplayName).toList(),
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

  private void log(String e) {
    events.add("t=" + tick + "  " + e);
    if (events.size() > 250) events.remove(0);
  }
}
