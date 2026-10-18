package com.queuecraft.strategy;

import com.queuecraft.model.*;
import java.util.*;

public final class ShortestQueueStrategy implements AssignmentStrategy {
  public ServiceCounter chooseCounter(Customer c, List<ServiceCounter> counters) {
    return counters.stream()
        .filter(x -> x.isEnabled() && x.getServiceType() == c.getRequestedService())
        .min(
            Comparator.comparingInt(ServiceCounter::getLoad).thenComparing(ServiceCounter::getName))
        .orElse(null);
  }
}
