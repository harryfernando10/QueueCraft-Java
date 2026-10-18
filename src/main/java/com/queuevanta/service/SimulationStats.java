package com.queuevanta.service;

/** Current customer counts and completed-customer timing averages for a simulation. */
public record SimulationStats(
        int generated,
        int waiting,
        int serving,
        int completed,
        double averageWait,
        double averageService,
        int maxQueue) {}
