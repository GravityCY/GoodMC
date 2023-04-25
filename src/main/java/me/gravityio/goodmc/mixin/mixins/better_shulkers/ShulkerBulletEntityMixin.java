package me.gravityio.goodmc.mixin.mixins.better_shulkers;

import me.gravityio.goodmc.tweaks.better_shulkers.BetterShulkersTweak;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBulletEntity.class)
public class ShulkerBulletEntityMixin {
    @Inject(method="onEntityHit", at = @At(value="NEW", target="net/minecraft/entity/effect/StatusEffectInstance"), cancellable = true)
    private void onShulkerShellHit(EntityHitResult entityHitResult, CallbackInfo ci)
    {
        LivingEntity livingEntity = (LivingEntity) entityHitResult.getEntity();
        if (EnchantmentHelper.getEquipmentLevel(BetterShulkersTweak.SHULKER_AFFINITY, livingEntity) > 0)
            ci.cancel();
    }
}