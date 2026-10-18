package com.queuevanta.model;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.regex.Pattern;

/** A single service station with one active customer and a FIFO waiting line. */
public final class ServiceCounter {
    private static final Pattern VALID_NAME = Pattern.compile("[\\p{L}][\\p{L}\\p{N} _-]{1,29}");

    private final String name;
    private final ServiceType serviceType;
    private final int serviceTicks;
    private final Queue<Customer> waiting = new ArrayDeque<>();
    private Customer currentCustomer;
    private long remainingTicks;
    private boolean enabled = true;
    private int completedCount;

    /** Creates a named counter with a supported service type and duration from 1 to 100 ticks. */
    public ServiceCounter(String name, ServiceType type, int ticks) {
        String normalizedName = name == null ? "" : name.trim();
        if (!isValidName(normalizedName)) {
            throw new IllegalArgumentException(
                    "Use 2–30 characters, starting with a letter; letters, numbers, spaces, _ and - are allowed.");
        }
        if (ticks < 1 || ticks > 100) {
            throw new IllegalArgumentException("Service duration must be 1–100 ticks.");
        }
        this.name = normalizedName;
        this.serviceType = Objects.requireNonNull(type, "Service type is required.");
        this.serviceTicks = ticks;
    }

    /** Returns whether a trimmed name follows Queuevanta's 2–30 character naming rule. */
    public static boolean isValidName(String name) {
        return name != null && VALID_NAME.matcher(name.trim()).matches();
    }

    /** Adds a waiting customer for the service this counter provides. */
    public void enqueue(Customer customer) {
        Objects.requireNonNull(customer, "Customer is required.");
        if (customer.getStatus() != CustomerStatus.WAITING) {
            throw new IllegalStateException("Only waiting customers can join a counter queue.");
        }
        if (customer.getRequestedService() != serviceType) {
            throw new IllegalArgumentException("Customer service does not match this counter.");
        }
        waiting.add(customer);
    }

    /** Removes and returns the next waiting customer, or {@code null} if unavailable or empty. */
    public Customer poll() {
        return enabled ? waiting.poll() : null;
    }

    /** Advances active service by a tick and returns its customer when service completes. */
    public Customer advance() {
        if (currentCustomer == null) {
            return null;
        }
        remainingTicks--;
        if (remainingTicks > 0) {
            return null;
        }
        Customer finished = currentCustomer;
        currentCustomer = null;
        completedCount++;
        return finished;
    }

    /** Starts a customer whose service transition has already been recorded. */
    public void start(Customer customer) {
        Objects.requireNonNull(customer, "Customer is required.");
        if (!enabled) {
            throw new IllegalStateException("A disabled counter cannot start a customer.");
        }
        if (currentCustomer != null) {
            throw new IllegalStateException("This counter is already serving a customer.");
        }
        if (customer.getStatus() != CustomerStatus.BEING_SERVED) {
            throw new IllegalStateException("Customer must transition to service before starting.");
        }
        if (customer.getRequestedService() != serviceType) {
            throw new IllegalArgumentException("Customer service does not match this counter.");
        }
        currentCustomer = customer;
        remainingTicks = serviceTicks;
    }

    /** Clears active and waiting customers and the counter's completed-customer count. */
    public void clear() {
        waiting.clear();
        currentCustomer = null;
        remainingTicks = 0;
        completedCount = 0;
    }

    public String getName() {
        return name;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public int getServiceTicks() {
        return serviceTicks;
    }

    /** Returns a defensive copy that preserves FIFO order. */
    public Queue<Customer> getWaiting() {
        return new ArrayDeque<>(waiting);
    }

    public int getWaitingCount() {
        return waiting.size();
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public long getRemainingTicks() {
        return remainingTicks;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public int getLoad() {
        return waiting.size() + (currentCustomer == null ? 0 : 1);
    }
}
