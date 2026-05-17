# Performance Guide

This mod's core design is performance-first. The following describes the main techniques used and how you can further tune or extend them.

Primary techniques
- Tick gating: heavy logic (intent resolution, packet gating, context consumption) runs once every `(performanceTickSkip + 1)` ticks.
- Per-tick caching: repeated results such as `BlockHitResult` for the same anchor position are cached for the current world tick.
- HUD removed: rendering and text allocation for HUD have been disabled.
- Hotbar checks are kept cheap and uncached because they are O(1) and avoid stale state.

Further optimizations (next steps)
- Replace short-lived `BlockPos`/`Vec3d` allocations with reusable mutable objects.
- Convert small history data structures to primitive-backed ring buffers to avoid GC churn.
- Centralize raycasts so multiple systems share a single cached result per tick.
- Avoid any string formatting in the hot path; only log when `debugEnabled`.

Profiling
- Attach Spark or VisualVM to a dev client: run a 60s scenario and compare allocations/tick before and after changes.
- Target: reduce transient allocations by 70–90% and lower 99th-frame time by at least 25%.

CI
- Add a microbenchmark job to CI that runs a headless client harness for a short allocation/throughput test.
