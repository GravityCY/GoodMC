package me.gravityio.goodmc.mixin.mod.view_bobbing;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public class ViewBobbingMixin {
    @ModifyVariable(
            method = "bobView",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 3)
    private float onBobView(float value) {
        return value * (GoodMC.CONFIG.all.view_bobbing_strength / 100f);
    }
}
