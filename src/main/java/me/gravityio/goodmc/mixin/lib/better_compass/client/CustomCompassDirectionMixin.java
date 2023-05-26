package me.gravityio.goodmc.mixin.lib.better_compass.client;

import me.gravityio.goodmc.lib.better_compass.CompassUtils;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes compasses point to a structure using the NBT tag `PointsTo:{BlockPos:{}, dimension:""}`
 */
@Mixin(ModelPredicateProviderRegistry.class)
public class CustomCompassDirectionMixin {

    @Inject(method = "method_43220", at = @At("HEAD"), cancellable = true)
    private static void registerCompassModelProvider(ClientWorld world, ItemStack compass, Entity entity, CallbackInfoReturnable<GlobalPos> cir) {
        if (CompassUtils.isPointingRandom(compass) && CompassUtils.getRandom(compass)) {
            cir.setReturnValue(null);
            return;
        }
        if (!CompassUtils.isPointingAtPosition(compass)) return;
        cir.setReturnValue(CompassUtils.getGlobalPosPoint(compass));
    }


}
