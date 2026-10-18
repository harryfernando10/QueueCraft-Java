# Changelog

## Unreleased
- Renamed the application, Java packages, Maven coordinates, artifact, and user-facing text to Queuevanta. The original 1.0.0 history remains under its QueueCraft name.
- Replaced the dark dashboard with a light grey and white layout, blue and teal accents, larger typography, clearer grouping, and responsive counter controls.
- Made customer service visible in each counter queue row and preserved valid existing counter names such as `yt`.
- Centralized counter-name rules and enforced lifecycle and service-type checks at the counter boundary.
- Reformatted the Java source, expanded public API documentation, and updated all product documentation and the Modules 1–7 mapping.
- Retained customer generation, shortest-load assignment, pause/resume/reset, counter availability, analytics warnings, event history, and CSV export.

## 1.0.0 — Initial QueueCraft implementation
- Original Swing campus queue simulator with counter management, sample scenario, synchronized engine, shortest-load assignment, statistics, and bounded activity history. The product was later renamed to Queuevanta.
- Maven/JUnit setup and initial project documentation.
