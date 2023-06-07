package me.gravityio.goodmc.mixin.mod.locator;

import me.gravityio.goodlib.util.MoveUpdater;
import me.gravityio.goodmc.tweaks.locator.impl.IMovementLocatable;
import me.gravityio.goodmc.tweaks.locator.impl.LocatableRegistry;
import me.gravityio.goodmc.tweaks.locator.impl.LocatableType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(Entity.class)
public abstract class LocatableMixin {
  private final Map<IMovementLocatable, MoveUpdater> moveUpdaters = new HashMap<>();
  private Vec3d lastPos;
  private double lastVel = 0;

  @Inject(method = "updatePosition", at = @At("HEAD"))
  private void onUpdatePosition(double x, double y, double z, CallbackInfo ci) {
    Entity self = (Entity) (Object) this;
    if (!(self instanceof ServerPlayerEntity serverPlayer)) return;

    Vec3d pos = new Vec3d(x, y, z);
    if (lastPos == null) lastPos = pos;
    double vel = Math.abs(pos.subtract(lastPos).horizontalLength());
    if (lastVel > 0.01 && vel == 0) vel = lastVel;

    for (IMovementLocatable locatable : LocatableRegistry.get(LocatableType.MOVEMENT_LOCATABLE)) {
      MoveUpdater move = moveUpdaters.computeIfAbsent(locatable, (a) -> new MoveUpdater());
      if (!locatable.hasMoved(move.tick(pos), vel)) continue;
      move.setOrigin();
      DefaultedList<ItemStack> items = serverPlayer.getInventory().main;
      for (int i = 0; i < 9; i++) {
        ItemStack stack = items.get(i);
        if (locatable.isLocatable(stack, serverPlayer))
          locatable.locate(stack, serverPlayer);
      }
    }

    lastPos = pos;
    lastVel = vel;
  }
}
