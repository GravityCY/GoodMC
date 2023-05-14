package me.gravityio.goodmc.mixin.mod.angry_mobs.client;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkeletonEntityRenderer.class)
public abstract class AngrySkeletonMixin extends BipedEntityRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>> {

    private static final Identifier ANGRY_TEXTURE = new Identifier("goodmc", "textures/entity/skeleton/angry_skeleton.png");

    public AngrySkeletonMixin(EntityRendererFactory.Context ctx, SkeletonEntityModel<AbstractSkeletonEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(at = @At("HEAD"), method = "getTexture(Lnet/minecraft/entity/mob/AbstractSkeletonEntity;)Lnet/minecraft/util/Identifier;", cancellable = true)
    private void getTexture(AbstractSkeletonEntity abstractSkeletonEntity, CallbackInfoReturnable<Identifier> info) {
        if (!GoodMC.config.angry_mobs) return;
        if (abstractSkeletonEntity.canPickUpLoot())
            info.setReturnValue(ANGRY_TEXTURE);
    }
}