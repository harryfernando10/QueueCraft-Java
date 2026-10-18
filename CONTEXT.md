# Queuevanta Development Context

Queuevanta is a Java 17+ Swing teaching simulator for configured campus service queues. Results are illustrative and are not forecasts of actual campus waiting times.

## Architecture

- `QueuevantaApplication` opens `QueuevantaFrame` on Swing's Event Dispatch Thread.
- `QueuevantaFrame` presents KPIs, counter cards, customer queues, activity, controls, estimated-wait warnings, and CSV export.
- `SimulationEngine` synchronizes state, tick progression, statistics, and bounded event history.
- `AssignmentStrategy` is implemented by `ShortestQueueStrategy`; assignment uses eligible counter load and alphabetical tie-breaking.
- `Customer` and `ServiceCounter` enforce lifecycle, service eligibility, and FIFO queues.
- `CounterSnapshot` detaches counter state for safe UI rendering; customers are labeled with their requested service.
- `SimulationRunner` advances ticks on one scheduled daemon worker and posts refreshes to the EDT.
- `CounterValidator`, `SimulationStats`, and `SimulationReportExporter` handle input validation, metrics, and CSV output.

## Build and test

Use JDK 17+ and the Maven Wrapper:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
.\mvnw.cmd exec:java
```

The Maven artifact is `target/queuevanta-1.0.0.jar`; it is not a bundled standalone application. The current suite has 10 JUnit tests.

## Important behavior

A fresh application launch seeds five demonstration counters and 14 customers. Reset clears customers, events, and statistics, then pauses while preserving all counter configuration and valid counter names. Disabling a counter retains its active customer and queue; queued work resumes when re-enabled. One tick is 400 ms of application time. Estimated wait is active remaining service plus one configured service duration for each waiting customer.

## Git and repository state

This work is on `feature/queuevanta-rebrand-ui`, based on the latest fetched `origin/main`. At the time of this handover, `origin` still points to `https://github.com/harryfernando10/Queuevanta.git`; the GitHub repository rename must be verified separately. Recheck `git status --short --branch`, `git log -1 --oneline`, and `git remote -v` before continuing.

Before finalizing, rerun tests and `clean package`, run `git diff --check`, review the full diff and stale-name search, commit the intended files, push the branch, then verify the repository rename and remote URL. GitHub CLI is not installed; a GitHub browser session may be available, otherwise rename through repository Settings → General.

See [architecture](docs/ARCHITECTURE.md), [testing](docs/TESTING.md), [development guide](docs/DEVELOPMENT.md), [roadmap](docs/ROADMAP.md), and [module mapping](docs/MODULE_MAPPING.md).

