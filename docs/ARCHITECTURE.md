# Architecture

Swing UI calls the synchronized in-memory `SimulationEngine`; `SimulationRunner` schedules one bounded background worker. UI refreshes are posted to Swing's Event Dispatch Thread. The engine owns ordered counters, customer history and bounded event history. `AssignmentStrategy` allows replaceable assignment behavior; `ShortestQueueStrategy` selects the enabled compatible counter with smallest active-plus-waiting load, alphabetically resolving ties. Each step advances all counters, so service happens in parallel. A tick is 400 ms of real time, not a campus minute. Reset clears customers and each counter under the engine monitor.

## Concurrency and lifecycle

The single-thread scheduled executor advances the engine every 400 ms. Engine methods are synchronized because button handlers on the EDT and the scheduled worker can access the same state. UI refresh requests use SwingUtilities.invokeLater. Pause cancels the scheduled task and marks the engine paused; reset pauses first, clears state under the engine monitor, then schedules a refresh. Closing the window cancels the task and shuts down the executor. The one-worker executor is bounded and daemonized.

## UI communication

The frame submits user actions to engine methods and reads immutable list snapshots and statistics for rendering. Tick work stays outside the EDT; Swing components are updated only on the EDT.

Packages: model (customer/counter/status/service), engine (simulation state and ticks), strategy (routing interface), service (validation/statistics/checked exception), concurrent (worker lifecycle), UI (Swing), app (entry point).
\n## Class relationships\n\n```mermaid\nclassDiagram\n  class Customer\n  class ServiceCounter\n  class SimulationEngine\n  class AssignmentStrategy {\n    <<interface>>\n    +chooseCounter(customer, counters)\n  }\n  class ShortestQueueStrategy\n  class SimulationRunner\n  class QueueCraftFrame\n  SimulationEngine o-- ServiceCounter\n  SimulationEngine o-- Customer\n  SimulationEngine --> AssignmentStrategy\n  AssignmentStrategy <|.. ShortestQueueStrategy\n  QueueCraftFrame --> SimulationEngine\n  QueueCraftFrame --> SimulationRunner\n  SimulationRunner --> SimulationEngine\n```\n