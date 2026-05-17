package dev.anchorlikemarlow.alm.mixin;

import dev.anchorlikemarlow.alm.ALMClient;
import dev.anchorlikemarlow.alm.anchor.AnchorContextManager;
import dev.anchorlikemarlow.alm.interaction.InteractionRouter;
import dev.anchorlikemarlow.alm.interaction.RouteOutcome;
import dev.anchorlikemarlow.alm.util.BlockStateChecks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void alm$redirectBlockUse(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        ALMClient runtime = ALMClient.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();
        if (runtime == null || client.world == null || client.player == null) {
            return;
        }

        if (!runtime.config().assistEnabled()) {
            return;
        }

        if (runtime.packetGuard().isSyntheticDispatchActive()) {
            return;
        }

        InteractionRouter router = runtime.interactionRouter();
        RouteOutcome outcome = router.routeBlockUse(client, (ClientPlayerInteractionManager) (Object) this, player, hand, hitResult);
        if (outcome.handled()) {
            cir.setReturnValue(outcome.result());
            cir.cancel();
        }
    }

    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void alm$trackAnchorPlacement(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        ALMClient runtime = ALMClient.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();
        if (runtime == null || client.world == null || client.player == null) {
            return;
        }

        ActionResult result = cir.getReturnValue();
        if (result != ActionResult.SUCCESS && result != ActionResult.CONSUME) {
            return;
        }

        if (!player.getStackInHand(hand).isOf(Items.RESPAWN_ANCHOR)) {
            return;
        }

        BlockPos placedPos = hitResult.getBlockPos().offset(hitResult.getSide());
        if (BlockStateChecks.isRespawnAnchor(client.world.getBlockState(placedPos))) {
            AnchorContextManager contextManager = runtime.anchorContextManager();
            contextManager.recordPlacementCandidate(placedPos, client.world.getTime());
        }
    }
}
