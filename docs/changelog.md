# Changelog

All notable changes to this project should be documented in this file.

Unreleased
- Performance: added tick gating (`performanceTickSkip`) to reduce per-tick cost
- Performance: per-tick raycast cache to avoid repeated `BlockHitResult` allocations
- Performance: removed HUD rendering and overlay allocation

2026-05-17
- Initial rebrand and performance-first refactor (commit c332724)
