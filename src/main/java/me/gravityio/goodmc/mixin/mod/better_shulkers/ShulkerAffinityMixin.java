package me.gravityio.goodmc.mixin.mod.better_shulkers;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.gravityio.goodmc.tweaks.better_shulkers.BetterShulkersTweak;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShulkerBulletEntity.class)
public class ShulkerAffinityMixin {
    @WrapWithCondition(method="onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z"))
    private boolean onShulkerShellHit(LivingEntity livingEntity, StatusEffectInstance effect, Entity entity)
    {
        return EnchantmentHelper.getEquipmentLevel(BetterShulkersTweak.SHULKER_AFFINITY, livingEntity) <= 0;
    }
}