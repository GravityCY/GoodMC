package me.gravityio.goodmc.mixin.mod.baby_grows;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "getScaleFactor", at = @At("RETURN"), cancellable = true)
    private void getGrowing(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof PassiveEntity passiveEntity) || !passiveEntity.isBaby()) return;
        float num = 0.5f * (2f - (float) passiveEntity.getBreedingAge() / PassiveEntity.BABY_AGE);
        cir.setReturnValue(num);
    }
}
