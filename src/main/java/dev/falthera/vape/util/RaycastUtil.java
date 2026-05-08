package dev.falthera.vape.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class RaycastUtil {
    private RaycastUtil() {
    }

    public static BlockHitResult anchorHitResult(ClientPlayerEntity player, BlockPos pos) {
        Vec3d center = Vec3d.ofCenter(pos);
        return new BlockHitResult(center, Direction.UP, pos, false);
    }
}
