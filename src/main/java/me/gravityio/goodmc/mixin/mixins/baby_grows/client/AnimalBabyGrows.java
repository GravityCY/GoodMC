package me.gravityio.goodmc.mixin.mixins.baby_grows.client;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class AnimalBabyGrows<T extends LivingEntity> extends EntityRenderer<T> {
    protected AnimalBabyGrows(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "scale", at = @At("HEAD"))
    private void scaleByAge(T entity, MatrixStack matrices, float amount, CallbackInfo ci) {
        if (!GoodMC.config.animal_aging) return;
        if (entity instanceof AnimalEntity) {
            if (!entity.isBaby()) return;
            float newScale = 1 + (entity.age / 24000f);
            matrices.scale(newScale, newScale, newScale);
            this.shadowRadius = 0.25f * newScale;
        }
    }
}