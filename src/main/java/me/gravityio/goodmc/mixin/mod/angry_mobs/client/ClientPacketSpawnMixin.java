package me.gravityio.goodmc.mixin.mod.angry_mobs.client;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.mixin.interfaces.ILootEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPacketSpawnMixin {

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.onSpawnPacket (Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V"),
            method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V",
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, EntityType<?> entityType, Entity entity) {
        if (!GoodMC.CONFIG.all.angry_mobs) return;
        if (!(entity instanceof MobEntity mobEntity)) return;
        mobEntity.setCanPickUpLoot(((ILootEntity) packet).getCanPickupLoot());
    }
}