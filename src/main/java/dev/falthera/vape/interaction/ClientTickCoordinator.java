package dev.falthera.vape.interaction;

import dev.falthera.vape.FaltheraVapeConfig;
import dev.falthera.vape.FaltheraVapeClient;
import dev.falthera.vape.anchor.AnchorContextManager;
import dev.falthera.vape.network.PacketGuard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import dev.falthera.vape.util.RaycastUtil;

public final class ClientTickCoordinator {
    private final FaltheraVapeConfig config;
    private final AnchorContextManager anchorContextManager;
    private final PacketGuard packetGuard;

    private int lastSelectedSlot = -1;
    private Item lastMainHandItem = null;
    private ClientWorld lastWorld = null;

    private boolean pendingTotemUse = false;
    private BlockPos pendingAnchorPos = null;
    private int restoreSlotAfterSequence = -1;
    private long pendingTotemTick = Long.MIN_VALUE;

    public ClientTickCoordinator(FaltheraVapeConfig config, AnchorContextManager anchorContextManager, PacketGuard packetGuard) {
        this.config = config;
        this.anchorContextManager = anchorContextManager;
        this.packetGuard = packetGuard;
    }

    public void tick(MinecraftClient client) {
        long startNanos = 0L;
        if (config.debugEnabled() && config.fastMode()) {
            startNanos = System.nanoTime();
        }
        // assist is permanently enabled; no keybinds or GUI toggles

        if (client.world == null || client.player == null) {
            lastWorld = client.world;
            lastSelectedSlot = -1;
            lastMainHandItem = null;
            clearPendingSequence();
            anchorContextManager.clear();
            packetGuard.clear();
            return;
        }

        if (client.world != lastWorld) {
            lastWorld = client.world;
            lastSelectedSlot = -1;
            lastMainHandItem = null;
            clearPendingSequence();
            anchorContextManager.clear();
            packetGuard.clear();
        }

        long tick = client.world.getTime();
        int selectedSlot = client.player.getInventory().getSelectedSlot();
        Item currentMainHandItem = client.player.getMainHandStack().getItem();

        if (selectedSlot != lastSelectedSlot || currentMainHandItem != lastMainHandItem) {
            anchorContextManager.onSelectedItemChanged(lastMainHandItem, currentMainHandItem, tick);
            lastSelectedSlot = selectedSlot;
            lastMainHandItem = currentMainHandItem;
        }

        anchorContextManager.tick(client.world, client.player, tick);
        packetGuard.tick(tick);

        if (pendingTotemUse) {
            runPendingTotemUse(client, tick);
        }

        // Fast automatic sequence: place glowstone, then switch/use totem on the anchor
        if (config.assistEnabled() && config.fastMode()) {
            var context = anchorContextManager.activeContext();
            if (context != null && context.confirmed() && !context.autoSequenceStarted() && tick - context.createdTick() >= 1L) {
                try {
                    ClientPlayerEntity player = client.player;
                    ClientPlayerInteractionManager interactionManager = client.interactionManager;
                    if (player != null && interactionManager != null && !pendingTotemUse) {
                        int backupSlot = player.getInventory().getSelectedSlot();

                        // 1) Try place glowstone if present in hotbar
                        int glowSlot = findHotbarSlot(player, Items.GLOWSTONE);
                        if (glowSlot >= 0) {
                            player.getInventory().setSelectedSlot(glowSlot);
                            if (packetGuard.beginSyntheticDispatch(context.anchorPos(), Hand.MAIN_HAND, tick)) {
                                ActionResult res = interactionManager.interactBlock(player, Hand.MAIN_HAND, RaycastUtil.anchorHitResult(player, context.anchorPos()));
                                packetGuard.endSyntheticDispatch();
                            }
                        }

                        // 2) Queue totem use for the next tick to avoid same-tick desync/ghosting.
                        pendingTotemUse = true;
                        pendingAnchorPos = context.anchorPos().toImmutable();
                        pendingTotemTick = tick + 1L;
                        restoreSlotAfterSequence = backupSlot;
                        context.setAutoSequenceStarted(true);
                    }
                } catch (Exception e) {
                    // don't crash the client; silently ignore failures
                    clearPendingSequence();
                }
            }
        }

        if (config.debugEnabled() && config.fastMode()) {
            long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
            FaltheraVapeClient.LOGGER.info("[fastMode][tick] tick={} durationMs={} worldPresent={} playerPresent={} assistEnabled={}", tick, elapsedMs, client.world != null, client.player != null, config.assistEnabled());
        }
    }

    private int findHotbarSlot(ClientPlayerEntity player, Item item) {
        for (int i = 0; i < 9; i++) {
            try {
                if (player.getInventory().getStack(i).getItem() == item) {
                    return i;
                }
            } catch (Exception ignored) {
            }
        }
        return -1;
    }

    private void runPendingTotemUse(MinecraftClient client, long tick) {
        if (tick < pendingTotemTick || pendingAnchorPos == null) {
            return;
        }

        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (player == null || interactionManager == null) {
            clearPendingSequence();
            return;
        }

        try {
            Hand useHand = Hand.MAIN_HAND;
            if (player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
                useHand = Hand.OFF_HAND;
            } else {
                int totemSlot = findHotbarSlot(player, Items.TOTEM_OF_UNDYING);
                if (totemSlot >= 0) {
                    player.getInventory().setSelectedSlot(totemSlot);
                } else {
                    if (restoreSlotAfterSequence >= 0) {
                        player.getInventory().setSelectedSlot(restoreSlotAfterSequence);
                    }
                    clearPendingSequence();
                    return;
                }
            }

            if (packetGuard.beginSyntheticDispatch(pendingAnchorPos, useHand, tick)) {
                interactionManager.interactBlock(player, useHand, RaycastUtil.anchorHitResult(player, pendingAnchorPos));
                packetGuard.endSyntheticDispatch();
            }
        } finally {
            if (restoreSlotAfterSequence >= 0) {
                player.getInventory().setSelectedSlot(restoreSlotAfterSequence);
            }
            anchorContextManager.consume();
            clearPendingSequence();
        }
    }

    private void clearPendingSequence() {
        pendingTotemUse = false;
        pendingAnchorPos = null;
        restoreSlotAfterSequence = -1;
        pendingTotemTick = Long.MIN_VALUE;
    }
}
