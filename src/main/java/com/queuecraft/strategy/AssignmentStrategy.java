package com.queuecraft.strategy;

import com.queuecraft.model.Customer;
import com.queuecraft.model.ServiceCounter;
import java.util.List;

public interface AssignmentStrategy {
  ServiceCounter chooseCounter(Customer customer, List<ServiceCounter> counters);
}
