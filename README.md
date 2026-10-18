# Queuevanta

### Campus Queue Simulation & Bottleneck Analytics

**Queuevanta** is an educational Java Swing desktop application that simulates campus service queues and demonstrates how customer demand, service duration, counter availability, and workload distribution affect queue behaviour.

It provides an interactive dashboard where users can generate customers, manage service counters, monitor queue lengths, observe bottlenecks, and analyse simulation statistics.

Queuevanta is designed for learning, experimentation, and demonstration. Its results are based on the application's simulation rules and configured assumptions; they are not predictions of actual campus waiting times.

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Dashboard](#dashboard)
- [How the Simulation Works](#how-the-simulation-works)
- [Counter Management](#counter-management)
- [Statistics and Bottleneck Analysis](#statistics-and-bottleneck-analysis)
- [Technology Stack](#technology-stack)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [Documentation](#documentation)
- [Current Limitations](#current-limitations)
- [Future Scope](#future-scope)
- [Learning Outcomes](#learning-outcomes)
- [Disclaimer](#disclaimer)

---

## Overview

Campus service areas such as canteens, libraries, student offices, and IT help desks handle customers with different service requirements and processing times. When demand exceeds available service capacity, waiting lines can grow and certain counters can become bottlenecks.

Queuevanta provides a controlled environment to explore these situations through simulation.

Users can generate individual customers or batches, assign customers to eligible counters, observe service progression, disable or restore counters, and monitor how the system responds to changes in capacity and workload.

The application combines an interactive graphical interface with a Java-based simulation engine, allowing users to observe the effects of queue-management decisions without requiring a real campus deployment or external data source.

## Key Features

### Simulation Controls
- Start or resume the simulation.
- Pause simulation progression.
- Reset customers, event history, and metrics while retaining counter configuration.
- Generate a single customer or a batch of customers.
- Select the service type for newly generated customers.

### Service Counter Management
- Create additional service counters.
- Enable or disable counters.
- Re-enable previously disabled counters.
- Remove counters subject to application safeguards.
- Configure supported service types and service durations.
- Validate counter configuration and prevent invalid operations.

### Queue Management
- FIFO (First In, First Out) waiting queues.
- Parallel service progression across enabled counters.
- Shortest-load customer assignment.
- Service-type eligibility checks.
- Preservation of assigned queues when a counter is disabled.
- Counter-level serving, waiting, and completed-customer information.

### Monitoring and Analytics
- Generated, waiting, serving, and completed customer counts.
- Average waiting time for completed customers.
- Estimated wait for each counter.
- Configurable bottleneck-warning threshold.
- Recent simulation event history.
- Maximum observed counter load.
- CSV summary export.

---

## Dashboard

The Queuevanta dashboard brings the main simulation information and controls into one desktop interface.

| Dashboard component | Purpose |
|---|---|
| KPI cards | Display generated, waiting, serving, completed, and average-wait metrics. |
| Counter cards | Show counter status, service configuration, workload, and estimated wait. |
| Customer queue panel | Display serving customers and FIFO waiting lines by counter. |
| Recent events | Provide a chronological view of simulation activity. |
| Simulation controls | Start, resume, pause, and reset the simulation. |
| Customer generation | Generate individual customers or batches for a selected service. |
| Configuration and export | Adjust warning thresholds, manage counters, and export a CSV summary. |

The interface uses a light visual theme, clear status indicators, and warning colours to make counter workload and bottlenecks easier to identify.

*Screenshots can be added to this section as the project evolves.*

---

## How the Simulation Works

### 1. Customer Generation

Users select a service type and generate one customer or a batch. Each customer is associated with a service requirement that determines which counters are eligible to serve them.

On a fresh launch, the application seeds five demonstration counters and fourteen customers.

### 2. Counter Eligibility

A customer can be assigned only to an enabled counter that supports the customer's requested service type.

Disabled counters are excluded from new assignments.

### 3. Shortest-Load Assignment

Queuevanta uses a shortest-load assignment strategy.

For each eligible counter, the assignment logic considers:

**Current load = active customer + waiting customers**

The customer is assigned to the eligible counter with the lowest combined active-and-waiting load. If multiple counters have the same load, the counter name is used as a deterministic alphabetical tie-break.

This strategy distributes new customers according to current workload rather than simply selecting the first available counter.

### 4. FIFO Queue Processing

Customers waiting at a counter are processed in First In, First Out order. The customer who has waited the longest is served next when the counter becomes available.

Enabled counters progress in parallel, allowing multiple customers to receive service simultaneously at different counters.

Once assigned, a customer remains associated with that counter under the current simulation rules.

### 5. Simulation Ticks

The simulation advances in discrete ticks. One tick corresponds to approximately **0.4 seconds of application time**.

A simulation tick is not equivalent to a campus minute or any other real-world service-time unit. It is an internal simulation step used to advance customer service and calculate metrics.

### 6. Counter State Changes

Users can disable and re-enable counters during a simulation. Disabled counters are excluded from new customer assignments, while their already assigned queues are retained according to the application's current rules.

Resetting clears customers, event history, and metrics while retaining the existing counters and their configuration. User-created counters are not silently renamed or removed.

---

## Counter Management

Queuevanta supports service counters configured for different campus services, including:

- Canteen
- Library
- Student office
- IT help desk

Demonstration counters are provided on launch. Users can add counters, configure service eligibility, change availability, and manage counters through the dashboard.

Counter validation and safeguards help prevent invalid configurations and unsafe management operations.

The exact behaviour of counter removal is governed by the application's validation rules and safeguards.

---

## Statistics and Bottleneck Analysis

Queuevanta provides several metrics to help users understand the simulated system.

| Metric | Meaning |
|---|---|
| Generated | Total number of customers generated in the simulation. |
| Waiting | Number of customers currently waiting for service. |
| Serving | Number of customers currently receiving service. |
| Completed | Number of customers who have completed service. |
| Average wait | Average waiting time among completed customers. |
| Estimated wait | Estimated remaining wait for a counter based on its active and waiting customers. |
| Maximum counter load | Largest active-plus-waiting load observed when assignments were made. |

### Estimated Waiting Time

The current estimate is calculated using the remaining service duration of the active customer, if present, plus one full configured service duration for each customer waiting in the queue.

The estimate is an approximation based on the configured service durations and current queue state.

### Bottleneck Warnings

Users can configure a waiting-time threshold. A warning appears when a counter has work and its estimated wait meets or exceeds that threshold.

This allows users to identify overloaded counters and observe how changes in counter availability or customer demand affect the simulation.

### Statistics Assumptions

- Average waiting time and average service duration use completed customers only.
- Maximum counter load records the largest active-plus-waiting load observed at assignment time.
- Utilization is not currently calculated because the application does not maintain an observation-window model of counter busy and idle time.

These definitions are important when interpreting the dashboard: the metrics describe the simulated workload, not actual campus operations.

---

## Data Export

Queuevanta supports exporting a CSV summary of simulation results.

The export can be used for further inspection in spreadsheet software or for documenting a demonstration.

The application is currently in-memory. Persistent scenario storage and automatic loading of saved simulation scenarios are not implemented.

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17+ | Core programming language |
| Java Swing | Desktop graphical user interface |
| Maven | Build and dependency management |
| Maven Wrapper | Reproducible Maven commands without requiring a separately installed Maven version |
| JUnit | Automated testing |

The implementation separates the user interface, simulation engine, domain models, assignment strategy, validation, statistics, and scheduled simulation runner into distinct packages.

---

## Requirements

Before running Queuevanta, ensure you have:

- **JDK 17 or later**
- A Windows PowerShell terminal or another compatible terminal
- Git, if you intend to clone the repository
- An internet connection for the initial dependency download, when required

The repository includes Maven Wrapper scripts, so a separate Maven installation is not required.

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/harryfernando10/Queuevanta.git
cd Queuevanta
```

### 2. Verify Java

```bash
java -version
```

Confirm that the installed Java version is 17 or later.

If Java is not available on your system `PATH`, configure `JAVA_HOME` to point to your JDK installation.

### 3. Run the Tests

In Windows PowerShell:

```powershell
.\mvnw.cmd test
```

### 4. Build the Application

```powershell
.\mvnw.cmd clean package
```

The Maven artifact is generated at:

```text
target/queuevanta-1.0.0.jar
```

This is a regular Maven JAR, not a shaded or self-contained distribution containing all dependencies.

### 5. Launch Queuevanta

```powershell
.\mvnw.cmd exec:java
```

This launches the desktop application through the Maven Exec plugin.

**Note:** Run each command separately from the project root. The test command executes the automated test suite, the package command builds the artifact, and the final command launches the application.

---

## Running Tests

Queuevanta includes automated tests for the simulation engine and queue-panel model.

Run the complete test suite with:

```powershell
.\mvnw.cmd test
```

For a clean build and test verification:

```powershell
.\mvnw.cmd clean package
```

The tests cover simulation behaviour and supported queue-panel logic, including customer ordering, serving details, counter labels, and relevant lifecycle rules.

Use the test output as the source of truth for the current test count and results.

---

## Project Structure

```text
Queuevanta/
├── .mvn/
│   └── wrapper/
├── docs/
│   ├── ARCHITECTURE.md
│   ├── DEVELOPMENT.md
│   ├── MODULE_MAPPING.md
│   ├── ROADMAP.md
│   ├── SRS.md
│   └── TESTING.md
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── queuevanta/
│   │               ├── app/
│   │               ├── concurrent/
│   │               ├── engine/
│   │               ├── model/
│   │               ├── service/
│   │               ├── strategy/
│   │               └── ui/
│   └── test/
│       └── java/
│           └── com/
│               └── queuevanta/
├── .gitignore
├── CHANGELOG.md
├── CONTEXT.md
├── README.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

### Package Responsibilities

| Package | Responsibility |
|---|---|
| `app` | Application entry point and initialization. |
| `concurrent` | Scheduled simulation execution. |
| `engine` | Simulation state, customer assignment, and tick progression. |
| `model` | Customer, counter, service, and simulation data models. |
| `service` | Validation, statistics, exceptions, and CSV export. |
| `strategy` | Assignment strategy abstraction and shortest-load implementation. |
| `ui` | Swing dashboard, counter views, customer queues, and user controls. |

The test source tree mirrors the main application package structure where appropriate.

---

## Documentation

Additional project documentation is available here:

- [Architecture](docs/ARCHITECTURE.md) — application components and their interactions.
- [Software Requirements Specification](docs/SRS.md) — functional requirements and system scope.
- [Development Guide](docs/DEVELOPMENT.md) — build and development workflow.
- [Testing Guide](docs/TESTING.md) — automated tests and verification.
- [Roadmap](docs/ROADMAP.md) — potential future improvements.
- [Development Context](CONTEXT.md) — project state and handover information.
- [Changelog](CHANGELOG.md) — implementation history and recorded changes.

---

## Current Limitations

Queuevanta is a demonstration simulator with a deliberately limited scope.

- Simulation state is held in memory and is not persisted between application launches.
- Customers are generated manually rather than through an automatic arrival process.
- The shortest-load assignment strategy is the currently implemented strategy.
- Estimated waiting time is based on configured durations and the current queue; it is not a statistical forecast.
- Counter utilization is not calculated because busy and idle time are not tracked over a defined observation window.
- There is no implemented scenario save/load and comparison workflow.
- Queue history charts and advanced analytics are not currently implemented.
- The application does not connect to real campus systems or use live operational data.

These limitations should be considered when interpreting simulation results or evaluating the application for use beyond an educational setting.

---

## Future Scope

Possible future extensions include:

- Automatic customer arrival models.
- Additional assignment strategies for comparing queue-management policies.
- Saving, loading, and comparing simulation scenarios.
- Queue-length and waiting-time history charts.
- Counter utilization and busy/idle-time analytics.
- More configurable simulation experiments.
- Additional test scenarios and usability improvements.

These are potential enhancements, not features currently guaranteed to be available.

---

## Learning Outcomes

Queuevanta provides a practical way to explore:

- Object-oriented programming in Java.
- Desktop application development using Java Swing.
- Separation of concerns and modular application architecture.
- Queues, FIFO processing, and workload assignment.
- Collections and domain modelling.
- Input validation and lifecycle management.
- Scheduled execution and simulation state management.
- Unit testing with JUnit.
- Maven-based project builds and dependency management.
- Interpreting basic performance metrics and bottleneck warnings.

The project can also support demonstrations of how software design decisions influence the behaviour of a simulated system.

---

## Disclaimer

Queuevanta is an educational project intended for learning and demonstration.

All customers, service durations, waiting times, and workload metrics are simulated. Results depend on the configured assumptions and application logic. They must not be treated as predictions, guarantees, or measurements of actual campus service performance.

---

## Author

**Harry Fernando**

GitHub: [@harryfernando10](https://github.com/harryfernando10)

Repository: [Queuevanta](https://github.com/harryfernando10/Queuevanta)
