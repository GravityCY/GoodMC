package me.gravityio.goodmc.mixin.lib.better_compass.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.gravityio.goodmc.lib.better_compass.CompassUtils;
import me.gravityio.goodmc.mixin.interfaces.IAngleInterpolatorAccessor;
import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.client.item.CompassAnglePredicateProvider.AngleInterpolator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(CompassAnglePredicateProvider.class)
public abstract class CustomCompassAngleMixin {

    private final Random random = new Random();

    @ModifyReturnValue(method = "getAngle", at = @At(value = "RETURN", ordinal = 1))
    private float setAngleRandom(float angle, ItemStack compass, ClientWorld world, int seed, Entity _e) {
        if (!CompassUtils.hasPointStrength(compass)) return angle;

        IAngleInterpolatorAccessor accessor = (IAngleInterpolatorAccessor) (Object) compass;
        AngleInterpolator stackInterpolator = accessor.getInterpolator();
        if (stackInterpolator == null)
            accessor.setInterpolator(stackInterpolator = new AngleInterpolator());
        if (stackInterpolator.shouldUpdate(world.getTime())) {
            double strength = CompassUtils.getPointStrength(compass);
            double d = random.nextDouble(0.5d);
            if (random.nextBoolean()) d = -d;
            stackInterpolator.update(world.getTime(), angle +  d - (strength * d));
        }
        return (float) stackInterpolator.value;
    }
}
