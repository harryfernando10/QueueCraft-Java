package com.queuevanta.model;

/** Lifecycle states used by customers in the simulation. */
public enum CustomerStatus {
    WAITING,
    BEING_SERVED,
    COMPLETED,
    CANCELLED
}
