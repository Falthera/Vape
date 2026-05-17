    package dev.anchorlikemarlow.alm.interaction;

import net.minecraft.util.ActionResult;

public record RouteOutcome(boolean handled, ActionResult result, String reason) {
    public static RouteOutcome passThrough() {
        return new RouteOutcome(false, ActionResult.PASS, "pass-through");
    }
}

