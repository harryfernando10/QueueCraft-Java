# QueueCraft — Campus Service Queue Simulator

QueueCraft is an educational Java Swing application for exploring how customer arrivals, service duration, and counter capacity affect queues in a campus service scenario. Its outputs describe the configured simulation; they do not predict actual campus waiting times.

## What you can explore

- Watch arrivals, waiting customers, active service, completions, and average wait in a live dashboard.
- Generate an individual customer or a batch for the selected service.
- Add, disable, re-enable, and remove counters. Disabling a counter preserves its current customer and waiting line; that line continues after the counter is enabled again.
- Compare service workload and estimated waiting time by counter. Set the estimated-wait threshold for a visible bottleneck warning.
- Pause without losing state, or reset customers and metrics while keeping counter configuration.
- Export a CSV summary with simulation metrics and per-counter workload.
- Review recent simulation events.

## Dashboard

The dashboard presents five headline measures: generated, waiting, serving, completed, and average wait. Counter cards show service type, duration, active customer, waiting count, completions, estimated wait, and any threshold warning. The customer queue is FIFO, and enabled counters advance in parallel.

There are no screenshots in the repository yet. All displayed data comes from the current in-memory simulation.

## How assignment and metrics work

The default strategy assigns a new customer to an enabled counter that supports the requested service and has the smallest active-plus-waiting load. If loads tie, the counter name sorts alphabetically. Customers do not move between counters after assignment.

One simulation tick is 0.4 seconds of application time; it is an abstract unit, not a campus minute. Average wait and average service use completed customers only. Maximum counter load is the largest active-plus-waiting load observed at assignment. Estimated counter wait is the remaining service ticks of its active customer plus one configured service duration for each customer waiting. The warning appears when that estimate meets or exceeds the user-selected threshold and the counter has work. These simple estimates and averages are illustrative, not operational forecasts. Utilization is not reported because the application does not track a defined observation window with busy and idle time.

## Requirements and commands

- JDK 17 or later.
- Maven Wrapper is included and downloads Maven 3.9.11 if needed.

From the repository root in PowerShell:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd test
.\mvnw.cmd exec:java
```

If Java is not on `PATH`, install a JDK 17+ and set `JAVA_HOME` to its installation directory.

## Project structure

```text
src/main/java/com/queuecraft/
  app/          Swing application entry point
  concurrent/   scheduled simulation runner and lifecycle
  engine/       synchronized simulation state, ticks, snapshots, and history
  model/        customers, counters, service types, and immutable counter snapshots
  service/      validation, metrics, exceptions, and CSV summary export
  strategy/     assignment abstraction and shortest-load strategy
  ui/           Swing dashboard and interaction handling
src/test/java/  JUnit 5 engine and CSV exporter tests
docs/           requirements, architecture, development, testing, roadmap, module mapping
```

## Documentation

- [Architecture and data flow](docs/ARCHITECTURE.md)
- [Software requirements](docs/SRS.md)
- [Development setup and Git workflow](docs/DEVELOPMENT.md)
- [Automated test coverage and gaps](docs/TESTING.md)
- [Java Modules 1–7 source mapping](docs/MODULE_MAPPING.md)
- [Completed and planned work](docs/ROADMAP.md)
- [Development handover context](CONTEXT.md)
- [Changelog](CHANGELOG.md)

## Limitations and next steps

QueueCraft currently uses an in-memory, deterministic model with manually generated arrivals. It has no scenario save/load, automatic arrival process, alternate assignment policies, scenario comparison, GUI automation, or utilization metric. Potential future work is tracked in the [roadmap](docs/ROADMAP.md); ideas there are not implemented features.

QueueCraft is an academic demonstration project, not an operational campus service or a source of real waiting-time estimates.
