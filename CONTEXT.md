# QueueCraft Development Context

QueueCraft is a Java 17+ Swing teaching simulator for configured campus service queues. It shows simulated behavior and does not predict actual campus waiting times.

## Current architecture

- `QueueCraftApplication` opens `QueueCraftFrame` on Swing's Event Dispatch Thread.
- `QueueCraftFrame` displays KPIs, counter cards, queue/activity panels, controls, wait warnings, and CSV export.
- `SimulationEngine` synchronizes state and delegates routing to `AssignmentStrategy`; the default is `ShortestQueueStrategy`.
- `Customer` and `ServiceCounter` enforce lifecycle and FIFO service queue behavior.
- `CounterSnapshot` captures detached counter display/analytics values under the engine lock.
- `SimulationRunner` advances ticks on a single scheduled daemon worker and posts UI refreshes to the EDT.
- `SimulationStats`, `CounterValidator`, and `SimulationReportExporter` provide metrics, configuration validation, and UTF-8 CSV output.

## Build and tests

Use JDK 17+ and the Maven Wrapper:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd test
.\mvnw.cmd exec:java
```

There are 9 JUnit tests across `SimulationEngineTest` and `SimulationReportExporterTest`.

## Important decisions and limits

Routing is shortest active-plus-waiting load, ties alphabetical. Disabled counters keep assigned customers; the active customer can finish, and the waiting queue resumes after re-enable. Estimated wait is active remaining ticks plus one full service duration per waiting customer. The warning threshold is expressed in ticks. The simulation advances each 400 ms; this is not a real campus-time estimate. No utilization, persistence, automatic arrivals, alternate policies, scenario comparison, charts, or GUI automation exists.

## Git state and next steps

Feature work is based on `origin/main` in `feature/queuecraft-product-enhancements`. Verify live state with `git status --short --branch` and `git log -1 --oneline`; branch, commit, and remote status change as work progresses. Before finalizing, rerun tests and `clean package`, run `git diff --check`, inspect the full diff, commit intended changes, and push the branch. GitHub CLI is not installed in the current environment, so PR creation may require the repository web interface.

See [architecture](docs/ARCHITECTURE.md), [testing](docs/TESTING.md), [development guide](docs/DEVELOPMENT.md), and [roadmap](docs/ROADMAP.md).
