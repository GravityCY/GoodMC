package me.gravityio.goodmc.mixin.mod.better_budding.client;

import com.mojang.authlib.GameProfile;
import me.gravityio.goodmc.GoodConfig;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.client.tweaks.better_amethyst.BetterAmethystTweak;
import me.gravityio.goodmc.lib.MoveUpdater;
import me.gravityio.goodmc.lib.better_compass.CompassUtils;
import me.gravityio.goodmc.lib.helper.NbtUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static me.gravityio.goodmc.lib.better_compass.CompassUtils.POINTS_TO;

@Mixin(ClientPlayerEntity.class)
public abstract class UpdateAmethystCompassMixin extends PlayerEntity {
    private static final String PREV = "PREV_"+POINTS_TO;
    private static final String INSIDE = "inAmethyst";
    private final MoveUpdater moveUpdater = new MoveUpdater();

    public UpdateAmethystCompassMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    private static void setPreviousPoint(ItemStack compass) {
        if (!CompassUtils.isPointingRandom(compass)) return;
        GoodMC.LOGGER.debug("[UpdateAmethystCompassMixin] Setting Previous Point");
        NbtUtils.internalCopy(compass.getNbt(), POINTS_TO, PREV);
    }

    private static boolean hasPreviousPoint(ItemStack compass) {
        if (!CompassUtils.isPointing(compass)) return false;
        GoodMC.LOGGER.debug("[UpdateAmethystCompassMixin] Has previous Point: {}", compass.getNbt().contains(PREV));
        return compass.getNbt().contains(PREV);
    }

    private static NbtElement getPreviousPoint(ItemStack compass) {
        if (!CompassUtils.isPointing(compass)) return null;
        NbtCompound nbt = compass.getNbt();
        GoodMC.LOGGER.debug("[UpdateAmethystCompassMixin] Previous Point: {}", nbt.get(PREV));
        return nbt.get(PREV);
    }


    private static void removePreviousPoint(ItemStack compass) {
        if (!CompassUtils.isPointing(compass)) return;
        compass.getNbt().remove(PREV);
        NbtCompound pointsTo = CompassUtils.getPointsTo(compass);
        GoodMC.LOGGER.debug("[UpdateAmethystCompassMixin] Removing previous Point");
        pointsTo.remove(PREV);
    }

    private static boolean isInsideAmethyst(ItemStack compass) {
        return compass.getNbt() != null && compass.getNbt().getBoolean(INSIDE);
    }

    /**
     * Every {@link BetterAmethystTweak#UPDATE_DISTANCE BetterAmethystTweak#UPDATE_DISTANCE} blocks moved sets compasses in player inventory to point to random if there's amethyst nearby
     */
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onMove(CallbackInfo ci) {
        if (!GoodConfig.INSTANCE.amethyst.geode_compass) return;
        if (moveUpdater.tick(this.getPos()) < GoodConfig.INSTANCE.amethyst.update_distance * (this.getVelocity().horizontalLength() + 1)) return;
        moveUpdater.setOrigin(this.getPos());

        List<ItemStack> compasses = BetterAmethystTweak.getCompasses(this.getInventory().main);
        if (compasses.isEmpty()) return;
        if (BetterAmethystTweak.nearAmethyst(this.world, this.getBlockPos())) {
            compasses.forEach(compass -> {
                if (CompassUtils.isPointingRandom(compass)) {
                    if (CompassUtils.getRandom(compass)) return;
                    setPreviousPoint(compass);
                }
                CompassUtils.setPointsToRandom(compass, true);
                compass.getNbt().putBoolean(INSIDE, true);
                GoodMC.LOGGER.debug("[UpdateAmethystCompassMixin] Walked into Amethyst: {}", compass.getNbt().toString());
            });
        } else {
            compasses.forEach(compass -> {
                if (!isInsideAmethyst(compass) || !CompassUtils.isPointingRandom(compass) || !CompassUtils.getRandom(compass)) return;
                if (hasPreviousPoint(compass)) {
                    NbtUtils.internalCopy(compass.getNbt(), PREV, POINTS_TO);
                    removePreviousPoint(compass);
                } else compass.getNbt().remove(POINTS_TO);
                compass.getNbt().remove(INSIDE);
                GoodMC.LOGGER.debug("[UpdateAmethystCompassMixin] Exited Amethyst: {}", compass.getNbt().toString());
            });
        }
    }
}
