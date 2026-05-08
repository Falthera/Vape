package dev.falthera.vape.render;

import dev.falthera.vape.FaltheraVapeConfig;
import dev.falthera.vape.anchor.AnchorContextManager;
import dev.falthera.vape.intent.IntentResolver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public final class AnchorHudOverlay {
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
        if (!config.hudEnabled() && !config.debugEnabled()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !anchorContextManager.isAssistEligible()) {
            return;
        }

        if (!config.hudInstantRender() && client.world == null) {
            return;
        }

        String text = config.debugEnabled()
            ? "Falthera VAPE: active (" + String.format("%.2f", anchorContextManager.confidenceScore()) + ")"
            : "Falthera VAPE: active";

        drawContext.drawTextWithShadow(
            client.textRenderer,
            Text.literal(text),
            6,
            6,
            0xFFFFFF
        );
    }
}
