# Java Modules 1–7 Mapping

This matrix describes concepts present in the current source. It does not imply every syllabus topic is used; unrelated language features are intentionally not added just to increase coverage.

| Module | Concepts | Evidence in QueueCraft | Notes |
|---|---|---|---|
| 1 — Java Basics | Program structure, variables, types, operators, conditions, loops, methods | `QueueCraftApplication`, `SimulationEngine`, `SimulationStats` | Swing handles input/output; there is no console input flow. |
| 2 — Object-Oriented Programming | Classes, objects, constructors, encapsulation, associations, polymorphism | `Customer`, `ServiceCounter`, `SimulationEngine`, `AssignmentStrategy` | Strategy implementations are used through an interface. |
| 3 — Inheritance | Inheritance, interfaces, abstract types, `super`, `final` | `AssignmentStrategy`, `ShortestQueueStrategy`, `QueueCraftFrame extends JFrame` | Interface polymorphism and Swing inheritance are present. No custom abstract class, explicit `super` business logic, or `final` parameter pattern is needed. |
| 4 — Collections | Arrays, `Set`, `List`, `Map`, `Vector` | Engine uses `LinkedHashMap`, `ArrayList`, immutable `List` snapshots; counters use `Queue`/`ArrayDeque` | The current design does not need `Set` or legacy `Vector`; list ordering and queue FIFO semantics are meaningful. |
| 5 — Strings | String operations, builders, buffers, regular expressions | UI and engine compose display/event text; `CounterValidator` uses a regular expression | `StringBuilder` is appropriate for local UI rendering. `StringBuffer` is unnecessary without shared mutable string state. |
| 6 — Exceptions | `try`, `catch`, checked/unchecked exceptions, exception hierarchy | `SimulationException`, domain `IllegalArgumentException`/`IllegalStateException`, validation and UI error handling | No `finally` block or `throws` is necessary in every operation; cleanup is managed by `AutoCloseable`. |
| 7 — Multithreading | Thread lifecycle/creation, synchronization, communication, interruption | `SimulationRunner` scheduled worker; synchronized `SimulationEngine`; Swing EDT refresh via `invokeLater`; shutdown on window close | Worker updates are communicated to Swing by posting a callback. Automated stress and GUI lifecycle tests are not present. |

## Demonstration path

Explain a customer arrival and shortest-load assignment, show how each counter advances on shared ticks, pause and resume, then reset. Emphasize that a tick is an arbitrary simulation unit and the application does not predict real campus waits.
