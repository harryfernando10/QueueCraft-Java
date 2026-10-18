package com.queuevanta.service;

/** Checked error for invalid operations in the simulation domain. */
public class SimulationException extends Exception {
    /** Creates an error with a message suitable for presentation to the user. */
    public SimulationException(String message) {
        super(message);
    }
}
