# Software Requirements Specification

## Purpose and scope

Queuevanta is an educational desktop simulator for exploring campus service queues using explicit, user-configured assumptions. It illustrates queue behavior; it does not predict real campus waiting times.

## Users

A student or instructor demonstrating how arrival volume, service duration, queue load, and counter availability interact.

## Functional requirements

- FR-01 Present simulation status, generated/waiting/serving/completed counts, average wait, counters, per-counter queues, and recent events.
- FR-02 Generate one customer or a batch of 1–100 customers for a selected service.
- FR-03 Route customers to an enabled compatible counter with the least active-plus-waiting load; break ties by counter name.
- FR-04 Add counters with validated unique names and service durations of 1–100 ticks.
- FR-05 Enable and disable counters. Preserve assigned customers when disabled; do not accept new customers or advance its waiting queue until re-enabled.
- FR-06 Remove only counters without active or waiting customers.
- FR-07 Start/resume tick progression, pause while retaining state, and confirm before reset clears customers, event history, and statistics while retaining counters.
- FR-08 Show estimated wait per counter and a warning when it meets a user-configured threshold.
- FR-09 Export summary and per-counter metrics to a UTF-8 CSV file.
- FR-10 Keep the newest 250 events.

## Non-functional requirements

- NFR-01 Use Java Swing and Java 17, with no additional runtime dependencies.
- NFR-02 Keep engine state protected by synchronization and detach view data into immutable snapshots.
- NFR-03 Keep simulation ticks off the EDT and update Swing widgets only on the EDT.
- NFR-04 Provide readable validation messages, tooltips, and reset/remove confirmations.
- NFR-05 Describe all times as simulation ticks and state the assumptions behind estimates and metrics.

## Assumptions and metric definitions

Every counter serves one customer at a time. Customers are assigned at generation and stay at their assigned counter. Enabled counters advance in parallel. One scheduled tick is 400 ms of application time and is not a real-time minute. Average wait and average service are over completed customers only. Maximum counter load is active plus waiting customers. Estimated wait is remaining active service plus a full configured service duration for every waiting customer. Utilization is not calculated because there is no observation-window model for busy and idle time.

## Constraints and out of scope

Simulation state is in memory and reset removes generated customers. There is no persistence, automatic/probabilistic arrival process, scenario comparison, alternate assignment strategy, machine learning, network access, or real-world prediction. Those ideas are not acceptance requirements for the current version.

## Acceptance criteria

- Demonstrate parallel service, a queue that grows with batch arrivals, completions, and wait metrics.
- A counter warning explains its estimated wait and the configured threshold.
- Disabling and re-enabling a loaded counter preserves its customers; removing a loaded counter is rejected.
- Pause retains the current simulation; reset requires confirmation, pauses, clears customer/event metrics, and keeps counters.
- A CSV export contains labeled summary and counter rows and handles invalid destinations with a visible message.
