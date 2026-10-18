# Java Modules 1–7 Mapping

This mapping links academic topics to real QueueCraft source. A module mapping is supplementary documentation, not a product requirement. The project does not add artificial code to demonstrate syllabus features.

| Module | Concepts | Status and source evidence | Notes |
|---|---|---|---|
| 1 — Java Basics | Program structure, variables, types, operators, conditions, loops, methods | **Implemented** — `QueueCraftApplication.main`, `SimulationEngine.step`, `SimulationStats`, `CounterSnapshot.estimatedWaitTicks` | Swing handles input/output; there is no console input flow. |
| 2 — Object-Oriented Programming | Classes, objects, constructors, encapsulation, associations, polymorphism | **Implemented** — `Customer`, `ServiceCounter`, `SimulationEngine`, `AssignmentStrategy`, `CounterSnapshot` | Routing policy is used through an interface. |
| 3 — Inheritance | Inheritance, interfaces, abstract types, `super`, `final` | **Partially Implemented** — `AssignmentStrategy`, `ShortestQueueStrategy`, `QueueCraftFrame extends JFrame`, `CounterRenderer extends DefaultListCellRenderer` | Interface polymorphism and Swing inheritance are present. **Not Used:** a custom abstract hierarchy and explicit `super` business logic add no value here. |
| 4 — Collections | Arrays, `Set`, `List`, `Map`, `Vector`, queues | **Partially Implemented** — `SimulationEngine` uses `LinkedHashMap`, `ArrayList`, `List.copyOf`; `ServiceCounter` uses `Queue`/`ArrayDeque`; `CounterSnapshot` stores an immutable list | **Not Used:** `Set` and legacy `Vector` are not needed; FIFO queue and ordered counter list have direct purpose. |
| 5 — Strings | String operations, builders, buffers, regular expressions | **Partially Implemented** — `QueueCraftFrame.renderQueues`, `SimulationEngine.log`, `CounterValidator.create`, `SimulationReportExporter.escape` | `StringBuilder` is used for reports and display. **Not Used:** `StringBuffer` has no shared mutable text use case. |
| 6 — Exceptions | `try`, `catch`, checked/unchecked exceptions, exception hierarchy | **Implemented** — `SimulationException`, `CounterValidator.create`, UI action handlers, `SimulationReportExporter.export` | `IOException` is reported at the UI. Cleanup uses `AutoCloseable`; a `finally` block is not required for every operation. |
| 7 — Multithreading | Thread lifecycle/creation, synchronization, communication, interruption | **Partially Implemented** — `SimulationRunner.start/pause/resume/reset/close`, synchronized `SimulationEngine`, `SwingUtilities.invokeLater` | Single scheduled worker and EDT communication are implemented. **Not Yet Implemented:** runner lifecycle automation, concurrency stress, and GUI automation. |

## Demonstration path

Explain customer arrival and shortest-load assignment, show parallel service and growing queues, pause and resume, review wait estimates and a threshold warning, export a CSV summary, and reset. Emphasize that ticks are abstract simulation units and outputs are not real campus forecasts.
