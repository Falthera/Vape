package dev.falthera.vape;

public final class FaltheraVapeConfig {
    private boolean assistEnabled = true;
    private boolean hudEnabled = false;
    private boolean debugEnabled = false;
    private int contextWindowTicks = 1;
    private float highConfidenceThreshold = 0.78f;
    private float mediumConfidenceThreshold = 0.45f;
    private boolean fastMode = false;
    private int fastModePacketThrottleTicks = 1;
    private int fastModeContextWindowTicks = 1;
    private boolean hudInstantRender = false;

    public boolean assistEnabled() {
        return assistEnabled;
    }

    public void toggleAssist() {
        assistEnabled = !assistEnabled;
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
        return fastMode;
    }

    public void setFastMode(boolean fastMode) {
        this.fastMode = fastMode;
    }

    public int fastModePacketThrottleTicks() {
        return fastModePacketThrottleTicks;
    }

    public void setFastModePacketThrottleTicks(int ticks) {
        this.fastModePacketThrottleTicks = ticks;
    }

    public int fastModeContextWindowTicks() {
        return fastModeContextWindowTicks;
    }

    public void setFastModeContextWindowTicks(int ticks) {
        this.fastModeContextWindowTicks = ticks;
    }

    public boolean hudInstantRender() {
        return hudInstantRender;
    }

    public void setHudInstantRender(boolean hudInstantRender) {
        this.hudInstantRender = hudInstantRender;
    }

    public float highConfidenceThreshold() {
        return highConfidenceThreshold;
    }

    public float mediumConfidenceThreshold() {
        return mediumConfidenceThreshold;
    }
}
