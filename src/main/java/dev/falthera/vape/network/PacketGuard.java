package dev.falthera.vape.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;

public final class PacketGuard {
    private boolean syntheticDispatchActive;
    private BlockPos lastDispatchPos;
    private Hand lastDispatchHand;
    private long lastDispatchTick = Long.MIN_VALUE;

    public boolean beginSyntheticDispatch(BlockPos pos, Hand hand, long tick) {
        if (syntheticDispatchActive) {
            return false;
        }
        if (lastDispatchPos != null && lastDispatchHand == hand && lastDispatchTick == tick && lastDispatchPos.equals(pos)) {
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
        if (!syntheticDispatchActive && tick - lastDispatchTick > 2L) {
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
