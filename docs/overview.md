# Overview

AnchorLikeMarlow (ALM) is a client-side Fabric mod that provides automated assistance for respawn-anchor interactions. It's designed for players who want safe, low-latency anchor combos while keeping client performance costs minimal.

This fork intentionally removes UI and user-facing controls (no HUD, no keybinds, no commands) and focuses only on optimized assist behavior.

Primary features:
- Fast, reliable totem dispatch on respawn anchors
- Pattern detection (safe vs direct placement)
- Minimal per-tick overhead via gating and caching
- Synthetic packet dispatch guarded by `PacketGuard`

Intended audience: competitive players and modders who require the lowest possible client-side overhead from automation.
