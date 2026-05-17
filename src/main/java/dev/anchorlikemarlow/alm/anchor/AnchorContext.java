package dev.anchorlikemarlow.alm.anchor;

import net.minecraft.util.math.BlockPos;

public final class AnchorContext {
    private final BlockPos anchorPos;
    private final long createdTick;
    private long lastTouchedTick;
    private float confidence;
    private boolean confirmed;
    private boolean glowstoneObserved;
    private boolean swappedOffGlowstoneObserved;
    private boolean autoSequenceStarted;

    public AnchorContext(BlockPos anchorPos, long createdTick) {
        this.anchorPos = anchorPos.toImmutable();
        this.createdTick = createdTick;
        this.lastTouchedTick = createdTick;
        this.confidence = 0.35f;
    }

    public BlockPos anchorPos() {
        return anchorPos;
    }

    public long createdTick() {
        return createdTick;
    }

    public long lastTouchedTick() {
        return lastTouchedTick;
    }

    public void touch(long tick) {
        lastTouchedTick = tick;
    }

    public float confidence() {
        return confidence;
    }

    public void addConfidence(float delta) {
        confidence = clamp(confidence + delta);
    }

    public boolean confirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
        if (confirmed) {
            addConfidence(0.25f);
        }
    }

    public boolean glowstoneObserved() {
        return glowstoneObserved;
    }

    public void setGlowstoneObserved(boolean glowstoneObserved) {
        this.glowstoneObserved = glowstoneObserved;
        if (glowstoneObserved) {
            addConfidence(0.20f);
        }
    }

    public boolean swappedOffGlowstoneObserved() {
        return swappedOffGlowstoneObserved;
    }

    public void setSwappedOffGlowstoneObserved(boolean swappedOffGlowstoneObserved) {
        this.swappedOffGlowstoneObserved = swappedOffGlowstoneObserved;
        if (swappedOffGlowstoneObserved) {
            addConfidence(0.20f);
        }
    }

    public boolean autoSequenceStarted() {
        return autoSequenceStarted;
    }

    public void setAutoSequenceStarted(boolean started) {
        this.autoSequenceStarted = started;
    }

    public boolean isExpired(long tick, int windowTicks) {
        return tick - lastTouchedTick > windowTicks;
    }

    public boolean isRecent(long tick, int windowTicks) {
        return tick - createdTick <= windowTicks;
    }

    public void decay(float amount) {
        confidence = clamp(confidence - amount);
    }

    private static float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}

