package dev.falthera.vape;

public final class FaltheraVapeConfig {
    private boolean assistEnabled = true;
    private boolean hudEnabled = false;
    private boolean debugEnabled = false;
    private int contextWindowTicks = 1;
    private float highConfidenceThreshold = 0.78f;
    private float mediumConfidenceThreshold = 0.45f;

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

    public float highConfidenceThreshold() {
        return highConfidenceThreshold;
    }

    public float mediumConfidenceThreshold() {
        return mediumConfidenceThreshold;
    }
}
