package me.gravityio.goodmc.mixin.mixins;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.mixin.interfaces.ICanPickupLoot;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * A Mixin that makes mobs that can pickup items render with a different texture
 */
@SuppressWarnings("ALL")
public class AngryMobsMixin {

    // Server
    @Mixin(EntitySpawnS2CPacket.class)
    public static class ServerPacketSpawnMixin implements ICanPickupLoot {

        private boolean canPickupLoot;

        @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/entity/Entity;)V")
        private void createPacket(Entity entity, CallbackInfo ci) {
            if (!GoodMC.config.angry_mobs) return;
            if (!(entity instanceof MobEntity mobEntity)) return;
            this.canPickupLoot = mobEntity.canPickUpLoot();
        }

        @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V")
        private void readPacket(PacketByteBuf buf, CallbackInfo ci) {
            if (!GoodMC.config.angry_mobs) return;
            this.canPickupLoot = buf.readBoolean();
        }

        @Inject(at = @At("RETURN"), method = "write")
        private void writePacket(PacketByteBuf buf, CallbackInfo ci) {
            if (!GoodMC.config.angry_mobs) return;
            buf.writeBoolean(this.canPickupLoot);
        }

        @Override
        public boolean getCanPickupLoot() {
            return this.canPickupLoot;
        }
    }

    // Client
    @Mixin(SkeletonEntityRenderer.class)
    public abstract static class AngrySkeletonMixin extends BipedEntityRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>> {

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

    @Mixin(ZombieBaseEntityRenderer.class)
    public abstract static class AngryZombieMixin extends BipedEntityRenderer<ZombieEntity, ZombieEntityModel<ZombieEntity>> {

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

    @Mixin(ClientPlayNetworkHandler.class)
    public static class ClientPacketSpawnMixin {

        @Inject(at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.onSpawnPacket (Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V"),
                method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V",
                locals = LocalCapture.CAPTURE_FAILHARD)
        private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, EntityType<?> entityType, Entity entity) {
            if (!GoodMC.config.angry_mobs) return;
            if (!(entity instanceof MobEntity mobEntity)) return;
            mobEntity.setCanPickUpLoot(((ICanPickupLoot) packet).getCanPickupLoot());
        }
    }


}
