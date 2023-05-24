package me.gravityio.goodmc.mixin.mod.mob_grows;

import me.gravityio.goodmc.GoodConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity {
    @Shadow protected int breedingAge;

    @Shadow public abstract boolean isBaby();

    @Shadow protected int forcedAge;

    private static final TrackedData<Integer> BREEDING_AGE = DataTracker.registerData(PassiveEntity.class, TrackedDataHandlerRegistry.INTEGER);

    protected PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void onPassiveEntityMixin(CallbackInfo ci) {
        this.dataTracker.startTracking(BREEDING_AGE, this.breedingAge);
    }

    @Inject(method = "getBreedingAge", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void getAgeFromTracker(CallbackInfoReturnable<Integer> cir) {
        if (!GoodConfig.INSTANCE.aging.mob_aging) return;
        cir.setReturnValue(this.dataTracker.get(BREEDING_AGE));
    }

    @Inject(method = "setBreedingAge", at = @At("TAIL"))
    private void updateBreedingAgeTracker(int age, CallbackInfo ci) {
        if (!GoodConfig.INSTANCE.aging.mob_aging) return;
        this.dataTracker.set(BREEDING_AGE, age);
    }

    @Inject(method = "onTrackedDataSet", at = @At("HEAD"))
    private void updateDimensions(TrackedData<?> data, CallbackInfo ci) {
        if (!GoodConfig.INSTANCE.aging.mob_aging) return;
        if (this.isBaby() && data.equals(BREEDING_AGE))
            this.calculateDimensions();
    }
}
