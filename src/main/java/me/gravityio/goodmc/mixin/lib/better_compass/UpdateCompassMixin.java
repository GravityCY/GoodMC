package me.gravityio.goodmc.mixin.lib.better_compass;

import me.gravityio.goodmc.lib.MoveUpdater;
import me.gravityio.goodmc.lib.better_compass.StructureLocatorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Every time the player moves a certain amount of blocks compasses pointing to structures are updated with the closest structure
 */

@Mixin(Entity.class)
public abstract class UpdateCompassMixin {
    private static final int distance = 100;
    private double lastVel = 0;
    private final MoveUpdater moveUpdater = new MoveUpdater();
    private Vec3d last = null;

    @Inject(method = "updatePosition", at = @At("HEAD"))
    private void onUpdatePosition(double x, double y, double z, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof ServerPlayerEntity serverPlayer)) return;

        Vec3d pos = new Vec3d(x, y, z);
        if (last == null) last = pos;
        double vel = Math.abs(pos.subtract(last).horizontalLength());
        if (lastVel > 0.01 && vel == 0) vel = lastVel;
        last = pos;
        lastVel = vel;
        if (moveUpdater.tick(pos) < distance * (vel + 1)) return;
        moveUpdater.setOrigin();

        ServerWorld serverWorld = serverPlayer.getWorld();
        for (int i = 0; i < 8; i++) {
            ItemStack stack = serverPlayer.getInventory().main.get(i);
            StructureLocatorUtils.updateLocator(stack, serverWorld, serverPlayer);
        }

    }
}
