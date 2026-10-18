# Development

Use JDK 17 and the Maven Wrapper. Run `.\mvnw.cmd clean package`; launch with `.\mvnw.cmd exec:java`. Packages are lowercase and domain classes use singular nouns. Add a service type in `ServiceType`, then provide a matching counter in the UI sample scenario. To add routing, implement `AssignmentStrategy` and inject it into `SimulationEngine`. Keep engine mutations synchronized and route UI updates through `SwingUtilities.invokeLater`. Use short-lived feature branches and merge reviewed work into main; pull requests are optional solo review checkpoints.
