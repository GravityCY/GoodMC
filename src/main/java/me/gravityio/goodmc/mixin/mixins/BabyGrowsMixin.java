package me.gravityio.goodmc.mixin.mixins;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A Mixin that makes most baby entities actually visually scale up as their age on the client increases<br><br>
 * The flaw with this is that it's only done on the client and the client doesn't actually know the age of the Entity, only the ticks it has been loaded by the client
 */
@SuppressWarnings("ALL")
public class BabyGrowsMixin {

    @Mixin(LivingEntityRenderer.class)
    public abstract static class AnimalBabyGrows<T extends LivingEntity> extends EntityRenderer {
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

    @Mixin(VillagerEntityRenderer.class)
    public abstract static class VillagerBabyGrows extends EntityRenderer {

        protected VillagerBabyGrows(EntityRendererFactory.Context ctx) {
            super(ctx);
        }

        @ModifyConstant(allow = 1, method = "scale(Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V",
                constant = @Constant(floatValue = 0.5f, ordinal = 0))
        private float scaleSizeByAge(float g, VillagerEntity entity) {
            if (!GoodMC.config.animal_aging) return 0.5f;
            return 0.5f * (1 + (entity.age / 24000f));
        }

        @ModifyConstant(allow = 1, method = "scale(Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V",
                constant = @Constant(floatValue = 0.25f, ordinal = 0))
        private float scaleShadowByAge(float g, VillagerEntity entity) {
            if (!GoodMC.config.animal_aging) return 0.25f;
            return 0.25f * (1 + (entity.age / 24000f));
        }
    }

}