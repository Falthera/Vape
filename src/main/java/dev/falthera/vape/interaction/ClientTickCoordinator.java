package dev.falthera.vape.interaction;

import dev.falthera.vape.FaltheraVapeConfig;
import dev.falthera.vape.anchor.AnchorContextManager;
import dev.falthera.vape.network.PacketGuard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.client.world.ClientWorld;

public final class ClientTickCoordinator {
    private final FaltheraVapeConfig config;
    private final AnchorContextManager anchorContextManager;
    private final PacketGuard packetGuard;

    private int lastSelectedSlot = -1;
    private Item lastMainHandItem = null;
    private ClientWorld lastWorld = null;

    public ClientTickCoordinator(FaltheraVapeConfig config, AnchorContextManager anchorContextManager, PacketGuard packetGuard) {
        this.config = config;
        this.anchorContextManager = anchorContextManager;
        this.packetGuard = packetGuard;
    }

    public void tick(MinecraftClient client, KeyBinding toggleAssistKey) {
        if (toggleAssistKey != null) {
            while (toggleAssistKey.wasPressed()) {
                config.toggleAssist();
            }
        }

        if (client.world == null || client.player == null) {
            lastWorld = client.world;
            lastSelectedSlot = -1;
            lastMainHandItem = null;
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
        int selectedSlot = client.player.getInventory().selectedSlot;
        Item currentMainHandItem = client.player.getMainHandStack().getItem();

        if (selectedSlot != lastSelectedSlot || currentMainHandItem != lastMainHandItem) {
            anchorContextManager.onSelectedItemChanged(lastMainHandItem, currentMainHandItem, tick);
            lastSelectedSlot = selectedSlot;
            lastMainHandItem = currentMainHandItem;
        }

        anchorContextManager.tick(client.world, client.player, tick);
        packetGuard.tick(tick);
    }
}
