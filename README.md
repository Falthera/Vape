# AnchorLikeMarlow — Ultra-Fast Respawn Anchor Assistance

A high-performance Minecraft Fabric mod that provides godspeed-level automatic respawn anchor assistance. Place an anchor, charge it with glowstone, and the mod handles the totem usage at lightning-fast speeds (≤10ms per sequence).

## Features

✨ **Ultra-Fast Anchor Assistance**
- Automatic respawn anchor combo execution at sub-10ms speeds
- Zero-delay same-tick packet dispatch
- Detects user's pattern (safe vs direct) per anchor placement and assists accordingly

🎯 **Intelligent Pattern Detection**
- **Safe Pattern**: If you place glowstone at leg level after charging the anchor, the mod recognizes it and optimizes for that
- **Direct Pattern**: If you skip leg glowstone and go straight to totem, the mod uses the direct sequence instead
- Adapts dynamically — each anchor placement is independent

⚡ **Performance Optimized**
- Same-tick execution (no artificial tick delays)
- Minimum retry overhead (1 retry vs 2)
- Zero configuration needed — always-on assist with no keybinds or GUI toggles
- Sub-millisecond per-tick overhead

🔒 **Safe & Reliable**
- Built-in packet throttling to prevent server spam
- Synthetic dispatch gating with `PacketGuard`
- Fallback logic for network latency

## Installation

### Requirements
- **Minecraft**: 1.21.11
- **Fabric Loader**: 0.19.2 or compatible
- **Java**: 17+

### Steps
1. Download the latest JAR from [Releases](https://github.com/Falthera/AnchorLikeMarlow/releases)
2. Place it in your `.minecraft/mods` folder
3. Launch Minecraft with the Fabric Loader
4. The mod is immediately active — no configuration needed

## Usage

### Why Multiple Totems?

In PvP combat, players typically carry **multiple totems of undying** for two reasons:
1. **Offensive Anchoring**: Chain multiple anchor combos against an opponent (double, triple, quadruple anchors)
2. **Defensive Survival**: If an opponent anchors you while you're anchoring them, you need a totem to survive the counter-pop

The extra totem ensures you don't die if both players anchor each other simultaneously — if you pop your totem from their anchor while mid-combo on them, you still have a totem for your own anchor sequence.

### Basic Anchor Combo (Direct Pattern)

1. Place a **respawn anchor** at player level
2. Charge it with **glowstone** (right-click the anchor with glowstone in hand)
3. Hold **totem of undying** in main or off-hand
4. **The mod will right-click the totem on the anchor** automatically

### Safe Anchor Combo (Safe Pattern)

1. Place a **respawn anchor** at player level
2. Charge it with **glowstone** (right-click the anchor with glowstone in hand)
3. Place **glowstone at your leg level** (beside/below the anchor)
4. Hold **totem of undying** in main or off-hand
5. **The mod will detect the leg glowstone and optimize the sequence** automatically

### Multi-Anchor Sequences

The mod supports **back-to-back anchor combos** at godspeed speeds:
- Place multiple anchors in quick succession
- Each anchor is independently detected and assisted
- Pattern (safe vs direct) is detected **per anchor**
- Totems are consumed naturally as you chain combos
- This enables the aggressive double/triple anchor offense that competitive players rely on

The mod adapts — if you use safe anchors on one placement and direct anchors on the next, each will use its correct sequence.

## Configuration

Configuration file: `.minecraft/config/anchor-like-marlow.json5`

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `assistEnabled` | boolean | `true` | Enable anchor assist (always-on) |
| `fastMode` | boolean | `true` | Ultra-fast optimization mode |
| `fastModePacketThrottleTicks` | int | `0` | Packet throttle (0 = no throttle) |
| `debugEnabled` | boolean | `false` | Log timing/debug info |

### Example Config
```json5
{
  assistEnabled: true,
  fastMode: true,
  fastModePacketThrottleTicks: 0,
  debugEnabled: false,
  hudInstantRender: true
}
```

## Performance

### Speed Metrics
- **Immediate totem success**: ~2-3ms per sequence
- **With retries**: ~5-10ms worst case
- **Per-tick overhead**: <0.5ms per game tick
- **Packet dispatch**: Same-frame (0-tick delay)

### Why It's Fast
- Zero artificial delays (same-tick execution)
- Minimal retry overhead (1 retry only)
- Direct packet dispatch with `PacketGuard`
- Live hotbar slot detection (no scanning overhead)
- Pattern detection happens per-anchor (not per-tick)

## How It Works

### Anchor Detection
The mod monitors your hotbar for:
- **Respawn Anchor** — Tracked via `AnchorContextManager`
- **Glowstone** — Detected live each tick
- **Totem of Undying** — Detected live each tick

### Sequence Execution
1. **Confirmation**: Anchor placement confirmed via packet interception & confidence scoring
2. **Pattern Detection**: Checks if glowstone exists at leg positions (safe vs direct)
3. **Totem Dispatch**: Immediately attempts right-click on the anchor with totem
4. **Retry Logic**: If initial attempt fails, retries once on next frame
5. **Cleanup**: Restores hotbar slot and marks anchor as processed

### Architecture
- **`ClientTickCoordinator`** — Main interaction loop (runs every tick)
- **`AnchorContextManager`** — Tracks active anchor placements
- **`PacketGuard`** — Gates synthetic block interactions
- **`InteractionRouter`** — Routes block-use intents to Interaction Manager
- **`BlockStateChecks`** — Real-time world state validation

## Building from Source

### Prerequisites
- JDK 17+
- Gradle 8.0+

### Steps
```bash
# Clone the repository
git clone https://github.com/Falthera/AnchorLikeMarlow.git
cd AnchorLikeMarlow

# Build the mod
./gradlew build

# Output JAR: build/libs/anchor-like-marlow-*.jar
```

## Releases & Versioning

Releases are automatically published from git tags with semantic versioning (vX.Y.Z):
- **Major (X)**: Breaking changes to API or core behavior
- **Minor (Y)**: New features (backwards compatible)
- **Patch (Z)**: Bug fixes and optimizations

Auto-versioning via GitHub Actions — merges to `main` automatically increment patch version and publish stable releases.

## Troubleshooting

### Anchor Assist Not Working
- Ensure `assistEnabled = true` in config
- Check that you have totem of undying in hotbar or off-hand
- Verify anchor is fully charged (should be glowing)
- Check debug logs: set `debugEnabled = true` and look for `[fastMode]` messages

### Totem Not Triggering
- Try holding totem in off-hand instead of main hand
- Ensure anchor is at player level (not above/below)
- Check that you have at least one glowstone for charging

### Server Packet Spam
- Reduce speed by increasing `fastModePacketThrottleTicks` to 1-2
- Disable `fastMode` entirely if packets are still excessive
- This mod is client-side only; server cannot see assist logic

### Mod Not Loading
- Verify Fabric Loader 0.19.2 is installed
- Check Minecraft version is exactly 1.21.11
- Look for errors in latest.log (`.minecraft/logs/`)

## Compatibility

- **Minecraft**: 1.21.11
- **Fabric Loader**: 0.19.2+
- **Other Mods**: Works with all client-side Fabric mods (no conflicts)

## License

MIT License — See LICENSE file for details

## Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/improvement`)
3. Commit changes with clear messages
4. Push to your fork
5. Open a pull request

## Support

- **Issues**: Report bugs on [GitHub Issues](https://github.com/Falthera/AnchorLikeMarlow/issues)
- **Discussions**: Ask questions on [GitHub Discussions](https://github.com/Falthera/AnchorLikeMarlow/discussions)

---

**AnchorLikeMarlow (ALM)** | **Lightning-fast Minecraft assistance** ⚡

Made with ❤️ for the Minecraft Fabric community.