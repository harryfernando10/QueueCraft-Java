# QueueCraft development context

## Purpose and current status
QueueCraft is a Java 17 Swing educational campus queue simulator. Initial implementation is on feature/initial-implementation; a baseline project setup commit is on local main. Maven Wrapper is included. Public GitHub repository: https://github.com/harryfernando10/QueueCraft-Java. Pull request #1 is open from feature/initial-implementation to main. The feature branch is pushed and tracks origin/feature/initial-implementation; main tracks origin/main.

## Architecture and key classes
- QueueCraftApplication: Swing startup on the Event Dispatch Thread.
- QueueCraftFrame: controls, sample scenario, counters, metrics and event presentation.
- SimulationEngine: synchronized in-memory state and simulation ticks.
- ServiceCounter, Customer, ServiceType, CustomerStatus: domain model.
- AssignmentStrategy / ShortestQueueStrategy: interchangeable counter routing.
- SimulationRunner: single scheduled daemon worker; posts refreshes to the EDT.
- CounterValidator: counter-name regex and uniqueness validation.

## Features
Sample counters/customers, counter management, batches, shortest-load assignment, pause/resume/reset, parallel counter ticks, statistics, and a 250-entry activity log are implemented.

## Commands
.\mvnw.cmd clean package, .\mvnw.cmd test, .\mvnw.cmd exec:java.

## Design decisions and limitations
A simulation tick is scheduled every 400 ms; it is not a real-world minute. Collections are in memory. Completed customer wait/service durations use tick differences. Utilization is absent because there is no observation window or idle-time tracking. The GUI process launched and responded, but visual inspection was unavailable through the current computer-use inventory. GUI responsiveness and executor shutdown need manual verification. No persistence or external runtime dependency is intended.

## Future agent instructions
Inspect current code before editing. Keep scope to Java Modules 1–7, avoid unrelated rewrites, preserve the synchronized engine/EDT boundary, run the tests, and update documentation. Do not claim a successful build or remote publication without verifying it.
