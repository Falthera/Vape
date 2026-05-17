package dev.anchorlikemarlow.alm.interaction;

import dev.anchorlikemarlow.alm.ALMConfig;
import dev.anchorlikemarlow.alm.anchor.AnchorContext;
import dev.anchorlikemarlow.alm.anchor.AnchorContextManager;
import dev.anchorlikemarlow.alm.intent.ConfidenceLevel;
import dev.anchorlikemarlow.alm.intent.IntentDecision;
import dev.anchorlikemarlow.alm.intent.IntentResolver;
import dev.anchorlikemarlow.alm.network.PacketGuard;
import dev.anchorlikemarlow.alm.util.BlockStateChecks;
import dev.anchorlikemarlow.alm.util.RaycastUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public final class InteractionRouter {
    private final ALMConfig config;
    private final AnchorContextManager anchorContextManager;
    private final IntentResolver intentResolver;
    private final PacketGuard packetGuard;

    public InteractionRouter(ALMConfig config, AnchorContextManager anchorContextManager, IntentResolver intentResolver, PacketGuard packetGuard) {
        this.config = config;
        this.anchorContextManager = anchorContextManager;
        this.intentResolver = intentResolver;
        this.packetGuard = packetGuard;
    }

    public RouteOutcome routeBlockUse(MinecraftClient client, ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (!config.assistEnabled() || packetGuard.isSyntheticDispatchActive()) {
            return RouteOutcome.passThrough();
        }

        ClientWorld world = client.world;
        if (world == null) {
            return RouteOutcome.passThrough();
        }

        AnchorContext context = anchorContextManager.activeContext();
        IntentDecision decision = intentResolver.resolve(player, world, player.getStackInHand(hand), hitResult, context, world.getTime());
        if (decision.level() != ConfidenceLevel.HIGH) {
            // In fast mode, accept MEDIUM confidence for quicker decisions
            if (!(config.fastMode() && decision.level() == ConfidenceLevel.MEDIUM)) {
                return RouteOutcome.passThrough();
            }
        }

        if (context == null || !BlockStateChecks.isRespawnAnchor(world.getBlockState(context.anchorPos()))) {
            return RouteOutcome.passThrough();
        }

        if (hitResult != null && BlockStateChecks.isRespawnAnchor(world.getBlockState(hitResult.getBlockPos()))) {
            return RouteOutcome.passThrough();
        }

        return performSyntheticUse(interactionManager, player, hand, context.anchorPos(), world.getTime(), "redirect-block-use");
    }

    public RouteOutcome routeItemUse(MinecraftClient client, ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, Hand hand) {
        if (!config.assistEnabled() || packetGuard.isSyntheticDispatchActive()) {
            return RouteOutcome.passThrough();
        }

        ClientWorld world = client.world;
        if (world == null) {
            return RouteOutcome.passThrough();
        }

        AnchorContext context = anchorContextManager.activeContext();
        if (context == null) {
            return RouteOutcome.passThrough();
        }

        IntentDecision decision = intentResolver.resolve(player, world, player.getStackInHand(hand), null, context, world.getTime());
        if (decision.level() != ConfidenceLevel.HIGH) {
            if (!(config.fastMode() && decision.level() == ConfidenceLevel.MEDIUM)) {
                return RouteOutcome.passThrough();
            }
        }

        if (!BlockStateChecks.isRespawnAnchor(world.getBlockState(context.anchorPos()))) {
            return RouteOutcome.passThrough();
        }

        return performSyntheticUse(interactionManager, player, hand, context.anchorPos(), world.getTime(), "redirect-item-use");
    }

    private RouteOutcome performSyntheticUse(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, Hand hand, BlockPos targetPos, long tick, String reason) {
        if (!packetGuard.beginSyntheticDispatch(targetPos, hand, tick)) {
            return RouteOutcome.passThrough();
        }

        if (config.debugEnabled() && config.fastMode()) {
            dev.anchorlikemarlow.alm.ALMClient.LOGGER.info("[fastMode] performSyntheticUse reason={} target={} tick={}", reason, targetPos, tick);
        }

        try {
            ActionResult result = interactionManager.interactBlock(player, hand, RaycastUtil.anchorHitResult(player, targetPos));
            if (result == ActionResult.SUCCESS || result == ActionResult.CONSUME) {
                anchorContextManager.consume();
            }
            return new RouteOutcome(true, result, reason);
        } finally {
            packetGuard.endSyntheticDispatch();
        }
    }
}
