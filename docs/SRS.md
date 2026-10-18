# Software Requirements Specification

## Purpose and scope
QueueCraft is an educational desktop simulator for comparing campus queue behavior. It stores data in memory and does not predict real campus waits.

## Users
A student or instructor demonstrating queues, service rates, and parallel counters.

## Functional requirements
- FR-01 Show service counters, active customers, waiting customer labels, status, statistics, and events.
- FR-02 Add, remove idle, enable, and disable counters.
- FR-03 Generate single customers or batches (1–100) for a service type.
- FR-04 Route customers to the enabled compatible counter with least active-plus-waiting load; break ties by name.
- FR-05 Start/resume, pause, and reset the tick simulation.
- FR-06 Complete service and calculate waiting and service duration from ticks.
- FR-07 Keep newest 250 events.

## Non-functional requirements
- NFR-01 Swing actions and simulation processing remain separated so the UI can respond during simulation.
- NFR-02 Shared engine operations are synchronized.
- NFR-03 Inputs receive readable validation errors.
- NFR-04 Build with Java 17 and Maven, with no runtime dependencies outside the JDK.
- NFR-05 Explain simulation metrics as simulated ticks, not real operational estimates.

## Constraints and assumptions
In-memory only; a tick lasts 400 ms; each counter serves one customer at a time; generated customers are assigned immediately and are not rerouted.

## Interaction and validation
Start with the sample counters/customers; start or pause; generate customers; optionally manage counters; reset to clear history. Counter names are 2–30 characters, begin with a letter, and then allow letters, digits, spaces, underscore, hyphen. Durations and batches are 1–100.

## Acceptance criteria
Demonstrate parallel service, a growing queue, completions and waiting metrics; incompatible or unavailable service yields a clear error; reset empties customers and pauses processing.

## Out of scope
Persistence, databases, APIs, networks, authentication, forecasting, later syllabus modules.
