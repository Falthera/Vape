# Troubleshooting

Common issues and how to resolve them.

CI compile errors (illegal character '\ufeff')
- Cause: UTF-8 BOM present in Java sources. Solution: ensure files are saved without BOM and add `.gitattributes` to enforce `utf-8`.

Mod not active
- Ensure Fabric Loader and Minecraft versions match the requirements.
- Check that the JAR is present in `.minecraft/mods` and that no dependency conflicts exist.

Assist seems delayed
- The `performanceTickSkip` configuration reduces how often heavy logic runs. Lower it to make the mod react faster.

Excessive packet dispatch
- Increase `fastModePacketThrottleTicks` to 1 or 2 to add a small delay between synthetic dispatches.

Further help
- Open an issue on GitHub with logs (`.minecraft/logs/latest.log`) and a short description of steps to reproduce.
