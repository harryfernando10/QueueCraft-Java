package com.queuevanta.strategy;

import com.queuevanta.model.Customer;
import com.queuevanta.model.ServiceCounter;
import java.util.List;

/** Chooses an eligible service counter for a newly generated customer. */
public interface AssignmentStrategy {
    /** Returns the selected counter or {@code null} if no eligible counter exists. */
    ServiceCounter chooseCounter(Customer customer, List<ServiceCounter> counters);
}
