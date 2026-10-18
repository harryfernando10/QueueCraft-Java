package com.queuevanta.service;

import com.queuevanta.model.ServiceCounter;
import com.queuevanta.model.ServiceType;
import java.util.Collection;
import java.util.Objects;

/** Validates user-provided counter configuration before it enters the engine. */
public final class CounterValidator {
    private CounterValidator() {
    }

    /** Creates a valid counter after checking its name against existing counters. */
    public static ServiceCounter create(
            String name, ServiceType type, int ticks, Collection<ServiceCounter> existing)
            throws SimulationException {
        if (!ServiceCounter.isValidName(name)) {
            throw new SimulationException(
                    "Use 2–30 characters, starting with a letter; letters, numbers, spaces, _ and - are allowed.");
        }
        Objects.requireNonNull(existing, "Existing counter list is required.");
        if (existing.stream().anyMatch(counter -> counter.getName().equalsIgnoreCase(name.trim()))) {
            throw new SimulationException("A counter with that name already exists.");
        }
        try {
            return new ServiceCounter(name, type, ticks);
        } catch (IllegalArgumentException exception) {
            throw new SimulationException(exception.getMessage());
        }
    }
}
