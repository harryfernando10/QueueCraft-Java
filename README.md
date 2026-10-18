# QueueCraft — Campus Service Queue & Bottleneck Simulator

QueueCraft is an educational Java Swing application for observing how service rates, queues, and multiple campus counters interact. It simulates service events; it does not predict real campus waiting times.

## Problem and objectives

Campus counters can become busy at peak times, but the effect of queue size, service duration, and parallel counters is not always obvious. QueueCraft makes those relationships visible through an interactive, in-memory model.

## Features

- Sample canteen, library, and student office counters with varied service durations.
- Add, remove idle, enable, and disable counters.
- Generate customer batches for a selected service.
- Pause, resume, and reset simulation processing.
- Assign each arrival to the compatible enabled counter with the smallest active-plus-waiting load.
- Show active customers, waiting counts, completion totals, and a bounded event log.
- Report generated/waiting/serving/completed totals, completed-customer average waiting/service ticks, and maximum queue load.

## Screenshots

No screenshots have been captured yet. Add verified application screenshots here after launching the GUI.

## Java concepts demonstrated

- Variables, conditions, loops, methods, and formatted summaries.
- Encapsulated customer and service-counter objects; enum-based state and service categories.
- AssignmentStrategy interface and a shortest-load implementation.
- List, Map, Queue, and ArrayDeque; this single-thread-owned collection role does not need legacy Vector.
- StringBuilder for the event view and a regex for counter-name validation. StringBuffer is unnecessary because no shared mutable string buffer is used.
- Checked SimulationException for recoverable business validation and unchecked exceptions for invalid domain transitions.
- ScheduledExecutorService, synchronized engine state, a worker/UI boundary, and EDT-only UI updates.

## Technology and requirements

Java 17+, Maven 3.9+, Swing (JDK), JUnit 5 for tests.

## Build, test, package, and run

From the project root:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd test
.\mvnw.cmd exec:java
```

To launch after packaging:

```powershell
.\mvnw.cmd exec:java
```

The Maven Wrapper downloads Maven 3.9.11 without requiring a global Maven installation. Set JAVA_HOME to a JDK 17+ installation if Java is not already on PATH.

## Usage

1. Start the app; sample counters and waiting guests appear immediately.
2. Click Start / resume to advance simulation ticks.
3. Watch active service, waiting queues, event log, and statistics update.
4. Generate batches from the service dropdown; each tick takes 0.4 real seconds and is an arbitrary simulation unit.
5. Enable/disable a selected counter to compare capacity, or add another counter to increase it.
6. Click Pause or Reset as needed. Reset clears customers and counter totals while preserving configured counters.

Counter names must contain 2–30 permitted characters (letter first, then letters/numbers/spaces/underscore/hyphen). Service durations and batch sizes are limited to 1–100.

## 3–5 minute demonstration

1. Start with the sample data and point out five counters (including the ready IT Help Desk counter) and their distinct service durations.
2. Start the simulation. Explain that independent counters advance on each shared tick, illustrating parallel service.
3. Generate a batch of 6 canteen customers and observe customers queue across compatible counters.
4. Let several complete; point out that average wait and service duration include completed customers only.
5. Pause. Add a second IT Help Desk counter or enable a disabled counter, then generate customers of that type. Compare the queue clearing pace.
6. Reset and show customer data, totals, and tick return to zero while configured counters remain.

## Metrics

- Average waiting time: arithmetic mean of arrival tick to service-start tick among completed customers.
- Average service duration: arithmetic mean of service-start to completion ticks among completed customers.
- Maximum queue: largest active-plus-waiting load observed at a counter immediately after assignment.
- Utilization: not included because no observation window or idle-time accounting is modeled.

Ticks do not represent minutes. A timer tick is scheduled every 400 ms, and service duration is expressed as a count of those ticks.

## Project structure

See docs/ARCHITECTURE.md for package roles and docs/SRS.md for current behavior.

## Testing

Run .\mvnw.cmd test or .\mvnw.cmd clean package. See docs/TESTING.md for coverage and unverified areas.

## Limitations and future enhancements

State exists only in memory. Arrivals are generated on demand; there is no probabilistic arrival model, cancellation, persistence, or utilization metric. Future work could add deterministic scenario presets, utilization over a defined observation window, and richer customer display without changing the assignment strategy interface.

## Solo development workflow

Keep main as the stable branch and develop on short feature branches. Review changes before merging. A pull request can be a solo review checkpoint but is optional. Keep commits focused and update documentation with behavior changes.
