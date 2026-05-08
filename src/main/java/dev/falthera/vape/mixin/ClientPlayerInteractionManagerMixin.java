package dev.falthera.vape.mixin;

import dev.falthera.vape.FaltheraVapeClient;
import dev.falthera.vape.anchor.AnchorContextManager;
import dev.falthera.vape.interaction.InteractionRouter;
import dev.falthera.vape.interaction.RouteOutcome;
import dev.falthera.vape.util.BlockStateChecks;
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
    private void faltheraVape$redirectBlockUse(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        FaltheraVapeClient runtime = FaltheraVapeClient.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();
        if (runtime == null || client.world == null || client.player == null) {
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
    private void faltheraVape$trackAnchorPlacement(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        FaltheraVapeClient runtime = FaltheraVapeClient.getInstance();
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
