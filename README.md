Falthera VAPE — Fast Mode

This mod includes an opt-in `fastMode` to reduce latency for interaction routing and HUD updates.

Config knobs (in `FaltheraVapeConfig`):

- `fastMode` (boolean) — default `false`. When true, enables fast-path behavior.
- `fastModePacketThrottleTicks` (int) — minimum ticks to throttle duplicate synthetic dispatches in fast mode (default `1`).
- `fastModeContextWindowTicks` (int) — context window used for anchor confirmation in fast mode (default `1`).
- `hudInstantRender` (boolean) — when true, HUD rendering bypasses some checks for faster display.

How to test

1. Enable debug logging in config: `config.setDebugEnabled(true)` or toggle via runtime code.
2. Turn on fast mode: `config.setFastMode(true)`.
3. Start the client and perform anchor interactions. Watch the logs for `[fastMode]` messages and `[fastMode][tick]` timing lines.

Notes

- `fastMode` is opt-in because it relaxes some safety thresholds (e.g., allowing MEDIUM confidence to trigger actions).
- If you encounter unexpected packet spam, increase `fastModePacketThrottleTicks` or disable `fastMode`.

If you want, I can try to run the Gradle build next (if Java/Gradle are available) or prepare unit tests. Tell me which you prefer.