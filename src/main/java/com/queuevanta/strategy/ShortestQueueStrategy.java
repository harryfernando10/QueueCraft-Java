package com.queuevanta.strategy;

import com.queuevanta.model.Customer;
import com.queuevanta.model.ServiceCounter;
import java.util.Comparator;
import java.util.List;

/** Assigns to the compatible enabled counter with the smallest active-plus-waiting load. */
public final class ShortestQueueStrategy implements AssignmentStrategy {
    /** Breaks equal-load ties by counter name for repeatable assignment. */
    @Override
    public ServiceCounter chooseCounter(Customer customer, List<ServiceCounter> counters) {
        return counters.stream()
                .filter(
                        counter ->
                                counter.isEnabled()
                                        && counter.getServiceType() == customer.getRequestedService())
                .min(
                        Comparator.comparingInt(ServiceCounter::getLoad)
                                .thenComparing(ServiceCounter::getName))
                .orElse(null);
    }
}
