# Queuevanta

### Campus Queue Simulation & Bottleneck Analytics

Queuevanta is an educational Java Swing application for exploring how customer volume, service durations, and counter capacity affect campus service queues. Results are simulated from the settings and assumptions shown in the app; Queuevanta does not predict actual campus waiting times.

## Features

- Bright dashboard with generated, waiting, serving, completed, and average-wait indicators.
- Demonstration counters for canteen, library, student office, and IT help services.
- Generate a single customer or a batch for a selected service.
- Add, disable, re-enable, and remove counters with validation and safeguards.
- FIFO waiting lines and parallel counter service.
- Shortest-load assignment with a deterministic counter-name tie-break.
- Per-counter estimated waits and a user-configurable bottleneck warning threshold.
- Pause/resume, reset with confirmation, bounded activity history, and UTF-8 CSV summary export.

## Dashboard and simulation assumptions

The dashboard contains KPI cards, counter workload cards, customer queues, and recent events. There are no screenshots in the repository yet.

New customers are assigned to an enabled counter that supports the requested service and has the lowest active-plus-waiting load. Alphabetical counter name breaks ties. A customer stays at its assigned counter. The waiting line is FIFO; enabled counters progress in parallel.

One simulation tick is 0.4 seconds of application time and is not a campus minute. Average wait and average service use completed customers only. Maximum counter load is the largest active-plus-waiting load seen at assignment. Estimated wait is active remaining service plus a full configured duration for each waiting customer. A bottleneck warning appears when that estimate meets the selected threshold and the counter has work. Utilization is not calculated because there is no observation-window busy/idle model.

On a fresh launch the application seeds five demonstration counters and 14 customers. Reset clears customers, event history, and metrics but keeps the counters and their configuration; user-created counters are not silently renamed or removed.

## Requirements and run

- JDK 17 or later.
- Maven Wrapper is included.

From the project root in PowerShell:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
.\mvnw.cmd exec:java
```

The first two commands run automated tests and build the Maven artifact. `exec:java` launches the desktop app. Set `JAVA_HOME` to a JDK 17+ installation if Java is not on `PATH`.

The Maven artifact is `target/queuevanta-1.0.0.jar`. It is a regular Maven JAR, not a shaded or bundled standalone distribution; run the desktop application with `exec:java`.

## Project structure

```text
src/main/java/com/queuevanta/
  app/          application entry point
  concurrent/   scheduled simulation runner
  engine/       synchronized simulation state and tick progression
  model/        customers, counters, services, and immutable snapshots
  service/      validation, statistics, exceptions, and CSV export
  strategy/     assignment interface and shortest-load strategy
  ui/           Swing dashboard
src/test/java/com/queuevanta/  JUnit tests
docs/                         requirements, architecture, testing, roadmap, mapping
```

## Documentation

- [Architecture](docs/ARCHITECTURE.md)
- [Software requirements](docs/SRS.md)
- [Development guide](docs/DEVELOPMENT.md)
- [Testing](docs/TESTING.md)
- [Java Modules 1–7 mapping](docs/MODULE_MAPPING.md)
- [Roadmap](docs/ROADMAP.md)
- [Development context](CONTEXT.md)
- [Changelog](CHANGELOG.md)

## Limitations and future work

The application is in-memory and uses manually generated deterministic arrivals. Automatic arrivals, alternative assignment strategies, scenario save/load and comparison, queue history charts, and utilization are not implemented. See the roadmap for ideas, not promised functionality.

Queuevanta is for learning and demonstration. Its simulated results are illustrative and are not forecasts for real campus operations.
