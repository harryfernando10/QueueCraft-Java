# QueueCraft — Campus Service Queue Simulator

QueueCraft is a Java Swing teaching application for exploring how service durations, customer queues, and multiple campus counters interact. It models generated arrivals and service events; it does not forecast actual campus waiting times.

## Problem and scope

QueueCraft makes queue behavior visible in a small, interactive desktop model. Students can compare compatible counters, observe FIFO waiting lines, and see how adding or disabling capacity affects this simulated workload. All state is in memory and ticks are abstract units.

## Features

- Sample canteen, library, student office, and IT help counters.
- Add counters, toggle availability, and remove idle counters.
- Generate service-specific customer batches.
- Assign each customer to an enabled compatible counter with the least active-plus-waiting load; ties resolve by counter name.
- Start/resume, pause, and reset the simulation.
- View active and waiting customers, per-counter configuration, statistics, and the latest 250 events.
- Measure completed customers' average waiting/service ticks and maximum observed counter load.

## Requirements and build

- JDK 17 or later.
- Maven Wrapper included; it downloads Maven 3.9.11 when needed.

From the repository root in PowerShell:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd test
.\mvnw.cmd exec:java
```

`exec:java` starts the Swing application. If Java is not on `PATH`, configure `JAVA_HOME` to a JDK 17+ installation.

## How the simulation works

New customers are assigned immediately to the least-loaded enabled counter that serves their requested service. Load is the active customer plus everyone waiting; the waiting queue is FIFO. At each running tick, all counters advance, so counters serve in parallel. A free enabled counter starts its next customer. A tick is scheduled every 400 ms; service durations and waiting times are counts of ticks, not minutes.

Average wait and average service are calculated over completed customers. Maximum queue is the largest active-plus-waiting load seen after an assignment. Utilization is not calculated because the program does not track an observation window or idle time.

## Project structure

```text
src/main/java/com/queuecraft/
  app/          Application entry point
  concurrent/   Scheduled simulation runner
  engine/       Synchronized simulation state and tick progression
  model/        Customer, counter, status, and service types
  service/      Validation, statistics, and checked simulation errors
  strategy/     Counter assignment interface and shortest-load policy
  ui/           Swing dashboard
src/test/java/  JUnit tests
docs/           Architecture, requirements, development, testing, mapping, roadmap
```

## Usage

1. Start the app and review the sample counters and generated customers.
2. Select a service and batch size, then choose **Generate batch**.
3. Use **Start / resume** and **Pause** to control tick progression.
4. Select a counter to enable/disable it or remove it when idle. **Add counter…** validates its name, service, and duration.
5. Use **Reset** to clear simulation customers, event history, and statistics while keeping configured counters.

Counter names must be 2–30 characters, start with a letter, and contain only letters, digits, spaces, underscores, or hyphens. Durations and batch sizes are 1–100.

## Java Modules 1–7

The project naturally demonstrates Java basics, OOP, interfaces/inheritance, collections, strings/regex, exceptions, and multithreading. Some syllabus features (such as `Vector`, `StringBuffer`, custom abstract classes, or a `finally` block) are not useful to this implementation and are intentionally absent. See [the verified module mapping](docs/MODULE_MAPPING.md).

## Documentation

- [Architecture](docs/ARCHITECTURE.md)
- [Software Requirements Specification](docs/SRS.md)
- [Development setup and workflow](docs/DEVELOPMENT.md)
- [Testing and coverage](docs/TESTING.md)
- [Java Modules 1–7 mapping](docs/MODULE_MAPPING.md)
- [Development roadmap](docs/ROADMAP.md)
- [Session handover context](CONTEXT.md)
- [Changelog](CHANGELOG.md)

## Tests and limitations

Run `.\mvnw.cmd test`; the current unit tests cover customer transitions, assignment, engine progression, pause/reset, validation, and basic statistics. Runner lifecycle and visual GUI behavior are not automated. The app has no persistence, probabilistic arrivals, cancellation, utilization metric, network access, or real-world prediction. No genuine screenshots are included yet.

QueueCraft is an academic demonstration project, not an operational campus service or a source of real waiting-time estimates.
