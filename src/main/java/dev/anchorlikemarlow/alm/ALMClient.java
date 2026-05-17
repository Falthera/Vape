package dev.anchorlikemarlow.alm;

import dev.anchorlikemarlow.alm.anchor.AnchorContextManager;
import dev.anchorlikemarlow.alm.interaction.ClientTickCoordinator;
import dev.anchorlikemarlow.alm.interaction.InteractionRouter;
import dev.anchorlikemarlow.alm.network.PacketGuard;
import dev.anchorlikemarlow.alm.intent.IntentResolver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ALMClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("AnchorLikeMarlow");

    private static ALMClient instance;

    private final ALMConfig config = new ALMConfig();
    private final AnchorContextManager anchorContextManager = new AnchorContextManager(config);
    private final PacketGuard packetGuard = new PacketGuard(config);
    private final IntentResolver intentResolver = new IntentResolver(config);
    private final InteractionRouter interactionRouter = new InteractionRouter(config, anchorContextManager, intentResolver, packetGuard);
    private final ClientTickCoordinator clientTickCoordinator = new ClientTickCoordinator(config, anchorContextManager, packetGuard);

    @Override
    public void onInitializeClient() {
        instance = this;
        ClientTickEvents.END_CLIENT_TICK.register(client -> clientTickCoordinator.tick(client));

        LOGGER.info("AnchorLikeMarlow client initialized");
    }

    public static ALMClient getInstance() {
        return instance;
    }

    public ALMConfig config() {
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

