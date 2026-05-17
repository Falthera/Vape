# Development

This section describes how to work on the mod, run a dev environment, and test changes locally.

Environment
- JDK 17+
- Gradle (wrapper included)
- Fabric Loom configured in build.gradle

Build
```bash
./gradlew build
```

Run client (recommended for debugging)
```bash
# Start a client with the dev environment
./gradlew runClient
```

Testing performance changes
1. Build and place the JAR in your `.minecraft/mods` folder for quick manual testing.
2. Use Spark or VisualVM to profile allocations and hotspots.
3. Make small, incremental commits when optimizing and measure before/after.

Code conventions
- Keep the main tick path allocation-free where possible.
- Avoid creating Tracked objects per tick; prefer reusable fields.
- Guard debug logging behind `config.debugEnabled()` to prevent expensive formatting in hot paths.

Contributing
- Fork → branch → PR. Describe performance goals and benchmarks in the PR body.
