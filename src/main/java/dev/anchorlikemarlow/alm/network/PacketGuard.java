package dev.anchorlikemarlow.alm.network;

import dev.anchorlikemarlow.alm.ALMConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;

public final class PacketGuard {
    private final ALMConfig config;

    private boolean syntheticDispatchActive;
    private BlockPos lastDispatchPos;
    private Hand lastDispatchHand;
    private long lastDispatchTick = Long.MIN_VALUE;

    public PacketGuard(ALMConfig config) {
        this.config = config;
    }

    public boolean beginSyntheticDispatch(BlockPos pos, Hand hand, long tick) {
        if (syntheticDispatchActive) {
            return false;
        }

        long throttle = config.fastMode() ? config.fastModePacketThrottleTicks() : 2L;
        // With throttle=0 (fast mode), allow same-tick chained dispatches.
        if (lastDispatchPos != null && lastDispatchHand == hand && lastDispatchPos.equals(pos) && tick - lastDispatchTick < throttle) {
            return false;
        }

        syntheticDispatchActive = true;
        lastDispatchPos = pos.toImmutable();
        lastDispatchHand = hand;
        lastDispatchTick = tick;
        return true;
    }

    public void endSyntheticDispatch() {
        syntheticDispatchActive = false;
    }

    public boolean isSyntheticDispatchActive() {
        return syntheticDispatchActive;
    }

    public void tick(long tick) {
        long throttle = config.fastMode() ? config.fastModePacketThrottleTicks() : 2L;
        if (!syntheticDispatchActive && tick - lastDispatchTick > throttle) {
            lastDispatchPos = null;
            lastDispatchHand = null;
        }
    }

    public void clear() {
        syntheticDispatchActive = false;
        lastDispatchPos = null;
        lastDispatchHand = null;
        lastDispatchTick = Long.MIN_VALUE;
    }
}

