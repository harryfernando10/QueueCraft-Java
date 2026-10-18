# Testing

## Commands

From the project root in PowerShell:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

## Automated tests

`src/test/java/com/queuevanta/SimulationEngineTest.java` verifies:

- Customer wait/service transitions, duration calculations, and invalid completion transition.
- Compatible shortest-load assignment and deterministic name tie-breaking.
- Completion counts, averages, empty metrics, and paused tick behavior.
- Reset behavior and disabled-counter handling.
- Duplicate-name and input validation.
- A disabled counter retains its waiting line, can be re-enabled, and yields a detached immutable snapshot with the expected estimated wait.
- The valid two-character counter name `yt` is preserved, and its queue snapshot labels a generated customer with the requested service.

`src/test/java/com/queuevanta/SimulationReportExporterTest.java` verifies:

- CSV summary output, unit labels, and quoting for counter names containing commas.
- An invalid destination (a directory) is rejected.

## Current verification and gaps

The current project test suite contains 10 JUnit tests. Build/test verification does not visually verify the Swing layout. There are no GUI automation tests, scheduled-runner lifecycle tests, concurrency stress tests, or direct executor leak tests. Manually start/resume, pause, generate a batch, add and disable a counter, re-enable it, export a CSV, reset, and close the window to review those paths.
