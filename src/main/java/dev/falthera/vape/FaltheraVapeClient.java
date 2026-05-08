package dev.falthera.vape;

import dev.falthera.vape.anchor.AnchorContextManager;
import dev.falthera.vape.interaction.ClientTickCoordinator;
import dev.falthera.vape.interaction.InteractionRouter;
import dev.falthera.vape.network.PacketGuard;
import dev.falthera.vape.render.AnchorHudOverlay;
import dev.falthera.vape.intent.IntentResolver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FaltheraVapeClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Falthera VAPE");

    private static FaltheraVapeClient instance;

    private final FaltheraVapeConfig config = new FaltheraVapeConfig();
    private final AnchorContextManager anchorContextManager = new AnchorContextManager(config);
    private final PacketGuard packetGuard = new PacketGuard();
    private final IntentResolver intentResolver = new IntentResolver(config);
    private final InteractionRouter interactionRouter = new InteractionRouter(config, anchorContextManager, intentResolver, packetGuard);
    private final ClientTickCoordinator clientTickCoordinator = new ClientTickCoordinator(config, anchorContextManager, packetGuard);
    private final AnchorHudOverlay hudOverlay = new AnchorHudOverlay(config, anchorContextManager, intentResolver);

    private KeyBinding toggleAssistKey;

    @Override
    public void onInitializeClient() {
        instance = this;
        toggleAssistKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.falthera-vape.toggle_assist",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            "category.falthera-vape"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> clientTickCoordinator.tick(client, toggleAssistKey));
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> hudOverlay.render(drawContext));

        LOGGER.info("Falthera VAPE client initialized");
    }

    public static FaltheraVapeClient getInstance() {
        return instance;
    }

    public FaltheraVapeConfig config() {
        return config;
    }

    public AnchorContextManager anchorContextManager() {
        return anchorContextManager;
    }

    public InteractionRouter interactionRouter() {
        return interactionRouter;
    }

    public PacketGuard packetGuard() {
        return packetGuard;
    }
}
