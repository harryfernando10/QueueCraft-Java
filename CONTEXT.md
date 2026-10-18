# QueueCraft Development Context

QueueCraft is a Java 17+ Swing teaching simulator. This file is a concise handover; verify branch and remote state with Git because those change over time.

## Architecture

- `QueueCraftApplication` opens the frame on Swing's Event Dispatch Thread.
- `QueueCraftFrame` presents controls and renders counter snapshots, queues, metrics, and events.
- `SimulationEngine` owns synchronized simulation state, assignment, ticks, and event history.
- `Customer` and `ServiceCounter` model customer transitions and FIFO service queues.
- `AssignmentStrategy` is implemented by `ShortestQueueStrategy`.
- `SimulationRunner` schedules ticks on a single daemon executor and posts refresh callbacks to the EDT.
- `CounterValidator` validates new counter configuration; `SimulationStats` is the metrics record.

## Build and tests

Use JDK 17+ and the included Maven Wrapper:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd test
.\mvnw.cmd exec:java
```

## Design decisions and limits

Data is held in memory. Each 400 ms scheduled tick advances all counters. Assignment considers enabled counters of the requested service and uses active-plus-waiting load, then name for ties. Completed customers supply average wait and service values. There is no real-world arrival model, persistence, utilization accounting, GUI automation, or concurrency stress suite.

Keep the engine synchronized and Swing component updates on the EDT. Close the runner when closing the frame. Preserve the Maven Wrapper and package structure unless a concrete need justifies a change. Do not add syllabus constructs that have no project purpose.

## Safe workflow

Inspect `git status`, branch, and remote before editing. Base feature work on current `origin/main`; never commit directly to `main`. Run the test suite, package build, and `git diff --check` after changes. See [development](docs/DEVELOPMENT.md), [architecture](docs/ARCHITECTURE.md), [tests](docs/TESTING.md), [module mapping](docs/MODULE_MAPPING.md), and [roadmap](docs/ROADMAP.md).
