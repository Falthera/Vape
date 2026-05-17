package dev.anchorlikemarlow.alm.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.BlockHitResult;

public final class RaycastUtil {
    private RaycastUtil() {
    }

    // Simple per-tick cache to avoid allocating identical BlockHitResult many times during a single tick.
    private static long cachedTick = Long.MIN_VALUE;
    private static BlockPos cachedPos = null;
    private static BlockHitResult cachedResult = null;

    public static BlockHitResult anchorHitResult(ClientPlayerEntity player, BlockPos pos) {
        try {
            if (player != null) {
                long tick = player.getEntityWorld().getTime();
                if (tick == cachedTick && pos != null && pos.equals(cachedPos) && cachedResult != null) {
                    return cachedResult;
                }
                Vec3d center = Vec3d.ofCenter(pos);
                BlockHitResult r = new BlockHitResult(center, Direction.UP, pos, false);
                cachedTick = tick;
                cachedPos = pos;
                cachedResult = r;
                return r;
            }
        } catch (Exception ignored) {
            // fallback if world access fails
        }

        Vec3d center = Vec3d.ofCenter(pos);
        return new BlockHitResult(center, Direction.UP, pos, false);
    }
}

