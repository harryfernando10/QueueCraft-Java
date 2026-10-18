package com.queuecraft.strategy;

import com.queuecraft.model.Customer;
import com.queuecraft.model.ServiceCounter;
import java.util.Comparator;
import java.util.List;

public final class ShortestQueueStrategy implements AssignmentStrategy {
  public ServiceCounter chooseCounter(Customer c, List<ServiceCounter> counters) {
    return counters.stream()
        .filter(x -> x.isEnabled() && x.getServiceType() == c.getRequestedService())
        .min(
            Comparator.comparingInt(ServiceCounter::getLoad).thenComparing(ServiceCounter::getName))
        .orElse(null);
  }
}
