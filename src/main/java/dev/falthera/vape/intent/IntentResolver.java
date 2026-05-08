package dev.falthera.vape.intent;

import dev.falthera.vape.FaltheraVapeConfig;
import dev.falthera.vape.anchor.AnchorContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;

public final class IntentResolver {
    private final FaltheraVapeConfig config;

    public IntentResolver(FaltheraVapeConfig config) {
        this.config = config;
    }

    public IntentDecision resolve(ClientPlayerEntity player, ClientWorld world, ItemStack stack, BlockHitResult hitResult, AnchorContext context, long tick) {
        if (!config.assistEnabled()) {
            return IntentDecision.low("assist-disabled");
        }

        if (context == null) {
            return IntentDecision.low("no-context");
        }

        boolean holdingGlowstone = stack.isOf(net.minecraft.item.Items.GLOWSTONE);
        if (!context.glowstoneObserved()) {
            if (!holdingGlowstone) {
                return IntentDecision.low("waiting-glowstone-charge");
            }
        } else {
            if (holdingGlowstone) {
                return IntentDecision.low("waiting-swap-off-glowstone");
            }
            if (!context.swappedOffGlowstoneObserved()) {
                return IntentDecision.low("swap-off-glowstone-not-observed");
            }
        }

        float score = context.confidence();
        if (context.confirmed()) {
            score += 0.20f;
        }
        if (context.glowstoneObserved()) {
            score += 0.15f;
        }
        if (context.swappedOffGlowstoneObserved()) {
            score += 0.20f;
        }
        if (context.isRecent(tick, config.contextWindowTicks())) {
            score += 0.10f;
        }

        if (!holdingGlowstone && context.glowstoneObserved()) {
            score += 0.30f;
        }

        if (hitResult != null) {
            if (world.getBlockState(hitResult.getBlockPos()).isOf(net.minecraft.block.Blocks.RESPAWN_ANCHOR)) {
                score += 0.35f;
            } else if (hitResult.getBlockPos().equals(context.anchorPos())) {
                score += 0.30f;
            }
        } else {
            score += 0.15f;
        }

        if (player.isSprinting()) {
            score -= 0.05f;
        }

        score = clamp(score);
        ConfidenceLevel level = score >= config.highConfidenceThreshold()
            ? ConfidenceLevel.HIGH
            : score >= config.mediumConfidenceThreshold()
                ? ConfidenceLevel.MEDIUM
                : ConfidenceLevel.LOW;

        return new IntentDecision(level, score, buildReason(level, hitResult, context));
    }

    private static String buildReason(ConfidenceLevel level, BlockHitResult hitResult, AnchorContext context) {
        if (level == ConfidenceLevel.HIGH) {
            if (context.swappedOffGlowstoneObserved()) {
                return "charged-anchor-swap-confirmed";
            }
            return hitResult == null ? "confirmed-anchor-context" : "anchor-hit-confirmed";
        }
        if (context.confirmed()) {
            return "provisional-anchor";
        }
        return "uncertain";
    }

    private static float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}
