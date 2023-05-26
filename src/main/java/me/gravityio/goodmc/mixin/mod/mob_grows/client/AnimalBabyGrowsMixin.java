package me.gravityio.goodmc.mixin.mod.mob_grows.client;

import me.gravityio.goodmc.GoodConfig;
import me.gravityio.goodmc.GoodConfig.AnimalAging.AgeMobOnly;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class AnimalBabyGrowsMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends EntityRenderer<T> {
//    @Shadow protected M model;

    protected AnimalBabyGrowsMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

//    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
//    private void onRender(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
//        if (!(livingEntity instanceof PassiveEntity passiveEntity) || !(this.model instanceof IAgeAccessor ageAccessor)) return;
//        ageAccessor.setAge(passiveEntity.getBreedingAge());
//    }

    @Inject(method = "scale", at = @At("HEAD"))
    private void scaleByAge(T entity, MatrixStack matrices, float amount, CallbackInfo ci) {
        if (!GoodConfig.INSTANCE.aging.mob_aging || GoodConfig.INSTANCE.aging.only != AgeMobOnly.ALL && GoodConfig.INSTANCE.aging.only != AgeMobOnly.ANIMALS) return;
        if (entity instanceof PassiveEntity passiveEntity) {
            if (!entity.isBaby()) return;
            float newScale = 2 - passiveEntity.getBreedingAge() / -24000f;
            matrices.scale(newScale, newScale, newScale);
            this.shadowRadius = 0.25f * newScale;
        }
    }
}