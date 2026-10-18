package com.queuecraft.model;

import java.util.*;

public final class ServiceCounter {
  private final String name;
  private final ServiceType serviceType;
  private final int serviceTicks;
  private final Queue<Customer> waiting = new ArrayDeque<>();
  private Customer currentCustomer;
  private long remainingTicks;
  private boolean enabled = true;
  private int completedCount;

  public ServiceCounter(String name, ServiceType type, int ticks) {
    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Counter name cannot be blank.");
    if (ticks < 1 || ticks > 100)
      throw new IllegalArgumentException("Service duration must be 1–100 ticks.");
    this.name = name.trim();
    serviceType = Objects.requireNonNull(type);
    serviceTicks = ticks;
  }

  public void enqueue(Customer c) {
    waiting.add(c);
  }

  public Customer poll() {
    return waiting.poll();
  }

  public Customer advance() {
    if (currentCustomer == null) return null;
    if (--remainingTicks <= 0) {
      Customer done = currentCustomer;
      currentCustomer = null;
      completedCount++;
      return done;
    }
    return null;
  }

  public void start(Customer c) {
    currentCustomer = c;
    remainingTicks = serviceTicks;
  }

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

  public void setEnabled(boolean v) {
    enabled = v;
  }

  public int getCompletedCount() {
    return completedCount;
  }

  public int getLoad() {
    return waiting.size() + (currentCustomer == null ? 0 : 1);
  }
}
