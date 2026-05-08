package dev.falthera.vape.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public final class BlockStateChecks {
    private BlockStateChecks() {
    }

    public static boolean isRespawnAnchor(BlockState state) {
        return state != null && state.isOf(Blocks.RESPAWN_ANCHOR);
    }
}
