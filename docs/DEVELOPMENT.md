# Development Guide

## Setup and run

Install a JDK 17 or later and use the checked-in Maven Wrapper. On Windows PowerShell:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd test
.\mvnw.cmd exec:java
```

Set `JAVA_HOME` if the JDK is not discoverable on `PATH`. The exec Maven goal launches `com.queuecraft.app.QueueCraftApplication`.

## Debugging and design

Open the repository as a Maven project in an IDE and run the application main class or `SimulationEngineTest`. Keep simulation state and assignment rules in the engine/model/strategy packages, not in Swing rendering code. Engine methods are synchronized because the EDT and scheduled worker share the engine. Post any worker-originated Swing updates with `SwingUtilities.invokeLater`; close the `SimulationRunner` when the frame closes.

## Java conventions

Use explicit imports, four-space indentation, descriptive names, short focused methods, and meaningful exception messages. Document public behavior and non-obvious synchronization or domain rules. Keep collections appropriate to their role: ordered map for counters, list snapshots for read access, FIFO queue for waiting customers. Avoid unused syllabus constructs and unnecessary dependencies.

## Contribution workflow

Check status and current remotes before work. Create a descriptive feature branch from up-to-date `origin/main`; do not work directly on `main` or rewrite shared history. Make focused changes, run tests and a clean package build, inspect the full diff, and commit only intended files. Document behavior changes alongside implementation.
