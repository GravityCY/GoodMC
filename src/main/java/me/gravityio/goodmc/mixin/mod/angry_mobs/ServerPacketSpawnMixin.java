package me.gravityio.goodmc.mixin.mod.angry_mobs;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.mixin.interfaces.ILootEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySpawnS2CPacket.class)
public class ServerPacketSpawnMixin implements ILootEntity {
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