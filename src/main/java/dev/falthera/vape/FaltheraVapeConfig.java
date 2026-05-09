package dev.falthera.vape;

public final class FaltheraVapeConfig {
    // Assist is permanently enabled in this build (no keybinds or toggles)
    private final boolean assistEnabled = true;
    private boolean hudEnabled = false;
    private boolean debugEnabled = false;
    private int contextWindowTicks = 1;
    private float highConfidenceThreshold = 0.78f;
    private float mediumConfidenceThreshold = 0.45f;
    // fastMode is now always true to enable lowest-latency code paths
    private final boolean fastMode = true;
    // zero/very-small throttle values to allow immediate synthetic dispatches
    private final int fastModePacketThrottleTicks = 0;
    private final int fastModeContextWindowTicks = 0;
    // enable instant HUD rendering when fast mode is active
    private final boolean hudInstantRender = true;

    public boolean assistEnabled() {
        return assistEnabled;
    }

    public void toggleAssist() {
        // no-op: assist is permanently enabled
    }

    public boolean hudEnabled() {
        return hudEnabled;
    }

    public void setHudEnabled(boolean hudEnabled) {
        this.hudEnabled = hudEnabled;
    }

    public boolean debugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public int contextWindowTicks() {
        return contextWindowTicks;
    }

    public boolean fastMode() {
        return true;
    }

    public void setFastMode(boolean fastMode) {
        // no-op: fast mode is permanently enabled in this build
    }

    public int fastModePacketThrottleTicks() {
        return fastModePacketThrottleTicks;
    }

    public void setFastModePacketThrottleTicks(int ticks) {
        // no-op: throttle is fixed for fast-mode
    }

    public int fastModeContextWindowTicks() {
        return fastModeContextWindowTicks;
    }

    public void setFastModeContextWindowTicks(int ticks) {
        // no-op: context window is fixed for fast-mode
    }

    public boolean hudInstantRender() {
        return hudInstantRender;
    }

    public void setHudInstantRender(boolean hudInstantRender) {
        // no-op: instant HUD rendering is fixed for fast-mode
    }

    public float highConfidenceThreshold() {
        return highConfidenceThreshold;
    }

    public float mediumConfidenceThreshold() {
        return mediumConfidenceThreshold;
    }
}
