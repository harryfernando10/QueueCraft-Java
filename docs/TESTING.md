# Testing

## Commands

From the project root:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

JUnit 5 tests live in `src/test/java/com/queuecraft/SimulationEngineTest.java`.

## Current automated coverage

- Customer waiting/service transitions, duration calculations, and invalid completion transition.
- Shortest-load selection, compatible services, and deterministic name tie-breaking.
- Customer completion, wait/service averages, queue totals, and empty statistics.
- Paused tick behavior, resume, reset, and disabled/unavailable counters.
- Duplicate counter names and counter-name validation.

## Gaps

There are no Swing GUI automation tests, scheduled-runner lifecycle tests, concurrency stress tests, or direct resource-leak tests. Manually start/resume, pause, generate a batch, toggle an idle counter, reset, and close the window to review those paths. Do not treat a successful compile as visual verification.
