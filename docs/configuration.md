# Configuration

This mod intentionally keeps configuration minimal. Default config values prioritize performance and immediate assist behavior.

Location
- User config is expected under: `.minecraft/config/anchor-like-marlow.json5`

Important options
- `assistEnabled` (boolean, default: `true`) — enable/disable the assist logic.
- `debugEnabled` (boolean, default: `false`) — enable debug logging and timing messages.
- `performanceTickSkip` (int, default: `3`) — how many ticks to skip between heavy processing cycles. Effective processing frequency = 1 / (performanceTickSkip + 1). Increase to reduce CPU load further.
- `fastModePacketThrottleTicks` (int, default: `0`) — lowest-level packet throttle; increase to add delay between synthetic dispatches.

Tuning
- Reduce `performanceTickSkip` to make the mod react faster at the cost of CPU and allocations.
- Increase `performanceTickSkip` to minimize CPU and GC overhead; values of `4–10` are recommended for ultra-low overhead.

Example config snippet
```json5
{
  assistEnabled: true,
  debugEnabled: false,
  performanceTickSkip: 3,
  fastModePacketThrottleTicks: 0
}
```
