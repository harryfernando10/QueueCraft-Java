# Development Guide

## Setup and run

Install JDK 17 or later and use the Maven Wrapper from the repository root in PowerShell:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd test
.\mvnw.cmd exec:java
```

If Java is not discoverable, set `JAVA_HOME` to the JDK installation folder and ensure its `bin` folder is on `PATH`. The exec Maven goal starts `com.queuecraft.app.QueueCraftApplication`.

## Debugging and design

Open the project as a Maven project in an IDE and run the application main class or JUnit tests. Keep assignment rules and simulation state in engine/model/strategy packages, not in Swing rendering code. Engine methods synchronize access shared by the EDT and scheduled worker. Render immutable `CounterSnapshot` data. Post worker-originated Swing updates through `SwingUtilities.invokeLater`, and close `SimulationRunner` when the frame closes.

The shortest-load policy is the default and only current routing strategy. Its tie-break is alphabetical by counter name. A disabled counter preserves work already assigned to it, but its waiting line pauses until re-enabled. Estimated wait is remaining active service plus configured service duration times waiting-customer count; the dashboard threshold is configured in ticks.

## Java conventions

Use explicit imports, consistent indentation, descriptive names, short focused methods, and meaningful exception messages. Document public behavior and non-obvious synchronization or domain rules. Keep collections aligned with their purpose: ordered counter map, immutable list snapshots, FIFO waiting queue. Avoid unused syllabus constructs and unnecessary dependencies.

## Git workflow

Check `git status`, branch, and remotes before work. Fetch the verified `origin` and create a feature branch from current `origin/main`; do not commit directly to `main` or rewrite shared history. Preserve local work. Make focused changes, run tests and a clean package build, run `git diff --check`, inspect the complete diff, and commit only intended files. Push feature branches and create a PR only when authenticated GitHub tooling is available.
