package me.gravityio.goodmc.mixin.mixins.angry_mobs.client;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieBaseEntityRenderer.class)
public abstract class AngryZombieMixin extends BipedEntityRenderer<ZombieEntity, ZombieEntityModel<ZombieEntity>> {

    private static final Identifier ANGRY_TEXTURE = new Identifier("goodmc", "textures/entity/zombie/angry_zombie.png");

    public AngryZombieMixin(EntityRendererFactory.Context ctx, ZombieEntityModel<ZombieEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(at = @At("HEAD"), method = "getTexture(Lnet/minecraft/entity/mob/ZombieEntity;)Lnet/minecraft/util/Identifier;", cancellable = true)
    private void getTexture(ZombieEntity zombieEntity, CallbackInfoReturnable<Identifier> info) {
        if (!GoodMC.config.angry_mobs) return;
        if (zombieEntity.canPickUpLoot())
            info.setReturnValue(ANGRY_TEXTURE);
    }
}