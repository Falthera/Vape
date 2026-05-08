package dev.falthera.vape.render;

import dev.falthera.vape.FaltheraVapeConfig;
import dev.falthera.vape.anchor.AnchorContextManager;
import dev.falthera.vape.intent.IntentResolver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AnchorHudOverlay {
    private static final Logger LOGGER = LoggerFactory.getLogger("Falthera VAPE [HUD]");
    private final FaltheraVapeConfig config;
    private final AnchorContextManager anchorContextManager;
    @SuppressWarnings("unused")
    private final IntentResolver intentResolver;

    public AnchorHudOverlay(FaltheraVapeConfig config, AnchorContextManager anchorContextManager, IntentResolver intentResolver) {
        this.config = config;
        this.anchorContextManager = anchorContextManager;
        this.intentResolver = intentResolver;
    }

    public void render(DrawContext drawContext) {
        try {
            // Validate draw context
            if (drawContext == null) {
                return;
            }

            // Skip rendering if both UI modes are disabled
            if (!config.hudEnabled() && !config.debugEnabled()) {
                return;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.textRenderer == null) {
                return;
            }

            // Only render if player exists or instant render is enabled
            if (client.player == null && !config.hudInstantRender()) {
                return;
            }

            // Skip if not eligible for assist display and not in debug mode
            if (!config.debugEnabled() && !anchorContextManager.isAssistEligible()) {
                return;
            }

            // Build display text
            String text = config.debugEnabled()
                ? "Falthera VAPE: active (" + String.format("%.2f", anchorContextManager.confidenceScore()) + ")"
                : "Falthera VAPE: active";

            // Render with shadow for better visibility
            drawContext.drawTextWithShadow(
                client.textRenderer,
                Text.literal(text),
                6,
                6,
                0xFFFFFF
            );
        } catch (Exception e) {
            // Prevent rendering exceptions from crashing the game
            LOGGER.error("Failed to render HUD overlay", e);
        }
    }
}
