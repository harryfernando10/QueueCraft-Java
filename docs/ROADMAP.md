# Development Roadmap

Progress reflects source currently present on `main` as inspected for this work. Planned work is not marked complete merely because it is listed here.

## Phase 1 — Working simulator and core Java concepts

**Implemented:** Swing application, campus service counters, FIFO waiting queues, shortest-load assignment with deterministic name tie-breaking, synchronized simulation engine, scheduled runner, tick-based statistics, bounded event history, Maven wrapper, and unit tests. See [Module Mapping](MODULE_MAPPING.md).

## Phase 2 — UI refinement, validation, and edge cases

**In progress:** Improve dashboard hierarchy, readability, empty states, control help, and counter/customer presentation while preserving engine rules. Continue validating actions and error paths; consider focused tests for lifecycle behavior where deterministic seams are available.

## Phase 3 — Relevant syllabus concepts

**Ongoing selectively:** Document concepts naturally demonstrated by the project. Add a syllabus concept only when it supports a real requirement and can be explained in the code. Do not add unused `Vector`, `StringBuffer`, inheritance trees, or artificial threading examples.

## Phase 4 — Optional extensions

Possible future work includes scenario presets, accessible counter tables, configurable tick interval, and utilization only after an explicit observation-window model is designed. Persistence, network services, forecasting, and real campus waiting-time estimates are outside current scope.
