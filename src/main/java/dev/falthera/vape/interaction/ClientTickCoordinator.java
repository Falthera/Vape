package dev.falthera.vape.interaction;

import dev.falthera.vape.FaltheraVapeConfig;
import dev.falthera.vape.FaltheraVapeClient;
import dev.falthera.vape.anchor.AnchorContextManager;
import dev.falthera.vape.network.PacketGuard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.client.world.ClientWorld;
import org.lwjgl.glfw.GLFW;

public final class ClientTickCoordinator {
    private final FaltheraVapeConfig config;
    private final AnchorContextManager anchorContextManager;
    private final PacketGuard packetGuard;

    private int lastSelectedSlot = -1;
    private Item lastMainHandItem = null;
    private ClientWorld lastWorld = null;
    private boolean wasTogglePressed = false;

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
        if (client.getWindow() != null) {
            boolean togglePressed = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_F8) == GLFW.GLFW_PRESS;
            if (togglePressed && !wasTogglePressed) {
                config.toggleAssist();
            }
            wasTogglePressed = togglePressed;
        }

        if (client.world == null || client.player == null) {
            lastWorld = client.world;
            lastSelectedSlot = -1;
            lastMainHandItem = null;
            wasTogglePressed = false;
            anchorContextManager.clear();
            packetGuard.clear();
            return;
        }

        if (client.world != lastWorld) {
            lastWorld = client.world;
            lastSelectedSlot = -1;
            lastMainHandItem = null;
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

        if (config.debugEnabled() && config.fastMode()) {
            long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
            FaltheraVapeClient.LOGGER.info("[fastMode][tick] tick={} durationMs={} worldPresent={} playerPresent={} assistEnabled={}", tick, elapsedMs, client.world != null, client.player != null, config.assistEnabled());
        }
    }
}
