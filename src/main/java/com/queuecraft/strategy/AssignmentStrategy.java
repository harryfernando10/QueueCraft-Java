package com.queuecraft.strategy;

import com.queuecraft.model.*;
import java.util.List;

public interface AssignmentStrategy {
  ServiceCounter chooseCounter(Customer customer, List<ServiceCounter> counters);
}
