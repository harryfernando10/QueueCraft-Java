# Development Roadmap

Status reflects implementation on the current feature branch. Planned work below is not described as current functionality.

## Completed in current implementation

- Swing dashboard with simulation state and KPI cards, counter workload cards, queue and event views, tooltips, and confirmation for reset/removal.
- Queuevanta visual identity with a cool light-gray background, white surfaces, blue/teal accents, larger functional typography, and clearer responsive layout.
- Individual and batch customer generation.
- Shortest active-plus-waiting-load routing across compatible enabled counters, with alphabetical tie-breaking.
- Safe counter disable/re-enable behavior that preserves assigned customers.
- Per-counter estimated-wait display and user-configurable warning threshold.
- UTF-8 CSV export for simulation and counter summaries.
- Immutable counter snapshots, engine tests, report-export tests, and project documentation.
- Queuevanta package/class/Maven rename and user-facing project documentation.

## Known gaps

- No automatic arrival process, reproducible random scenarios, alternate routing strategy, scenario persistence, or scenario comparison.
- No queue trend charts or scenario-based workload comparison.
- No utilization measure: the engine does not collect busy/idle time for a defined observation window.
- No Swing GUI automation, scheduled-runner lifecycle test, or concurrency stress test.
- Build and tests do not confirm visual quality across operating systems or display scaling.

## Possible future work

1. Add deterministic engine seams and tests for scheduled-runner pause/resume/shutdown behavior.
2. Consider alternate strategies behind `AssignmentStrategy` after adding comparative tests and a clear UI choice.
3. Consider an optional seeded arrival model or saved scenario only if repeatability and comparison have a clear teaching benefit.
4. Add actual queue history and charts only after defining collection intervals and metric units.

No future item should claim actual campus forecasts or scientific prediction.
