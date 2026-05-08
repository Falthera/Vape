package dev.falthera.vape.anchor;

import dev.falthera.vape.FaltheraVapeConfig;
import dev.falthera.vape.util.BlockStateChecks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public final class AnchorContextManager {
    private final FaltheraVapeConfig config;
    private AnchorContext activeContext;

    public AnchorContextManager(FaltheraVapeConfig config) {
        this.config = config;
    }

    public void recordPlacementCandidate(BlockPos anchorPos, long tick) {
        activeContext = new AnchorContext(anchorPos, tick);
    }

    public void onSelectedItemChanged(Item previousItem, Item currentItem, long tick) {
        if (activeContext == null) {
            return;
        }

        activeContext.touch(tick);

        if (currentItem == Items.GLOWSTONE) {
            activeContext.setGlowstoneObserved(true);
            return;
        }

        if (currentItem == Items.RESPAWN_ANCHOR) {
            activeContext.addConfidence(0.10f);
            return;
        }

        if (previousItem == Items.RESPAWN_ANCHOR && currentItem == Items.GLOWSTONE) {
            activeContext.setGlowstoneObserved(true);
            return;
        }

        if (!activeContext.glowstoneObserved()) {
            clear();
            return;
        }

        if (previousItem == Items.GLOWSTONE && currentItem != Items.GLOWSTONE) {
            activeContext.setSwappedOffGlowstoneObserved(true);
        }

        activeContext.addConfidence(0.08f);
    }

    public void tick(ClientWorld world, ClientPlayerEntity player, long tick) {
        if (activeContext == null) {
            return;
        }

        if (world == null || player == null) {
            clear();
            return;
        }

        int window = config.fastMode() ? config.fastModeContextWindowTicks() : config.contextWindowTicks();
        if (activeContext.isExpired(tick, window)) {
            clear();
            return;
        }

        if (BlockStateChecks.isRespawnAnchor(world.getBlockState(activeContext.anchorPos()))) {
            activeContext.setConfirmed(true);
            activeContext.touch(tick);
        } else if (tick - activeContext.createdTick() > (config.fastMode() ? config.fastModeContextWindowTicks() : 2L)) {
            clear();
        } else {
            activeContext.decay(0.03f);
        }
    }

    public boolean isAssistEligible() {
        return activeContext != null;
    }

    public AnchorContext activeContext() {
        return activeContext;
    }

    public float confidenceScore() {
        return activeContext == null ? 0.0f : activeContext.confidence();
    }

    public void consume() {
        clear();
    }

    public void clear() {
        activeContext = null;
    }
}
