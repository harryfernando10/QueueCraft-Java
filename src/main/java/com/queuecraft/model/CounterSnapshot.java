package com.queuecraft.model;

import java.util.List;

/** Immutable counter data for analytics and display outside the engine lock. */
public record CounterSnapshot(
    String name,
    ServiceType serviceType,
    int serviceTicks,
    boolean enabled,
    String activeCustomer,
    long remainingTicks,
    List<String> waitingCustomers,
    int completed) {

  public CounterSnapshot {
    waitingCustomers = List.copyOf(waitingCustomers);
  }

  public int load() {
    return waitingCustomers.size() + (activeCustomer == null ? 0 : 1);
  }

  /** Estimates wait as remaining active service plus one full duration per queued customer. */
  public long estimatedWaitTicks() {
    return remainingTicks + (long) waitingCustomers.size() * serviceTicks;
  }
}
