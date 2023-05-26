package me.gravityio.goodmc.lib.better_compass;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.lib.helper.NbtUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * Utilities that helps with the tweak that makes compasses point to any block position
 */
public class CompassUtils {

    public static final String POINTS_TO = "PointsTo";
    public static final String BLOCK_POS = "BlockPos";
    public static final String DIMENSION = "dimension";
    public static final String STRENGTH = "strength";
    public static final String RANDOM = "random";

    /**
     * Returns whether the {@link GlobalPos} has `PointsTo.BlockPos` and `PointsTo.dimension` NBT
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static GlobalPos getGlobalPosPoint(ItemStack compass) {
        if (!CompassUtils.isPointingAtPosition(compass)) return null;
        NbtCompound pointsToComp = compass.getNbt().getCompound(POINTS_TO);
        BlockPos blockPos = net.minecraft.nbt.NbtHelper.toBlockPos(pointsToComp.getCompound(BLOCK_POS));
        NbtElement dimensionElem = pointsToComp.get(DIMENSION);

        Optional<RegistryKey<World>> world = World.CODEC.parse(NbtOps.INSTANCE, dimensionElem).result();
        return world.map(worldRegistryKey -> GlobalPos.create(worldRegistryKey, blockPos)).orElse(null);
    }

    /**
     * Gets or creates the `PointsTo` {@link NbtCompound}
     * @param compass {@link ItemStack}
     * @return {@link NbtCompound}
     */
    public static NbtCompound getOrCreatePointsTo(ItemStack compass) {
        return NbtUtils.getOrCreate(compass.getOrCreateNbt(), POINTS_TO);
    }

    /**
     * Sets `PointsTo.dimension` and `PointsTo.BlockPos`
     * @param compass {@link ItemStack}
     * @param pos {@link BlockPos}
     * @param dimension {@link Identifier}
     */
    public static void setPoint(ItemStack compass, BlockPos pos, Identifier dimension) {
        CompassUtils.setPointPosition(compass, pos);
        CompassUtils.setPointDimension(compass, dimension);
    }

    /**
     * Gets the `PointsTo` {@link NbtCompound}
     * @param compass {@link ItemStack}
     * @return {@link NbtCompound}
     */
    public static NbtCompound getPointsTo(ItemStack compass) {
        if (!isPointing(compass)) return null;
        return compass.getNbt().getCompound(POINTS_TO);
    }

    /**
     * Returns whether the compass has `PointsTo` NBT
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointing(ItemStack compass) {
        NbtCompound nbt = compass.getNbt();
        return nbt != null && nbt.contains(POINTS_TO);
    }

    /**
     * Sets the `PointsTo.dimension`
     * @param compass {@link ItemStack}
     * @param dimension {@link Identifier}
     */
    public static void setPointDimension(ItemStack compass, Identifier dimension) {
        GoodMC.LOGGER.debug("[CompassUtils] Setting compass point dimension to: {}", dimension);
        CompassUtils.getOrCreatePointsTo(compass).putString(DIMENSION, dimension.toString());
    }

    /**
     * Gets the `PointsTo.dimension` {@link Identifier}
     * @param compass {@link ItemStack}
     * @return {@link Identifier}
     */
    public static Identifier getPointDimension(ItemStack compass) {
        if (!CompassUtils.isPointing(compass)) return null;
        return new Identifier(CompassUtils.getPointsTo(compass).getString(DIMENSION));
    }

    /**
     * Returns whether the compass has `PointsTo` NBT and `PointsTo.dimension`
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointingAtDimension(ItemStack compass) {
        return isPointing(compass) && getPointsTo(compass).contains(DIMENSION);
    }

    /**
     * Sets the `PointsTo.BlockPos`
     * @param compass {@link ItemStack}
     * @param pos {@link BlockPos}
     */
    public static void setPointPosition(ItemStack compass, BlockPos pos) {
        GoodMC.LOGGER.debug("[CompassUtils] Setting compass point position to: {}", pos);
        CompassUtils.getOrCreatePointsTo(compass).put(BLOCK_POS, net.minecraft.nbt.NbtHelper.fromBlockPos(pos));
    }

    /**
     * Gets the `PointsTo.BlockPos` {@link BlockPos}
     * @param compass {@link ItemStack}
     * @return {@link BlockPos}
     */
    public static BlockPos getPointPosition(ItemStack compass) {
        if (!CompassUtils.isPointingAtPosition(compass)) return null;
        return net.minecraft.nbt.NbtHelper.toBlockPos(getPointsTo(compass).getCompound(BLOCK_POS));
    }

    /**
     * Returns whether the compass has `PointsTo.BlockPos` and `PointsTo.dimension` NBT
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointingAtPosition(ItemStack compass) {
        if (!CompassUtils.isPointing(compass)) return false;
        NbtCompound pointsTo = compass.getSubNbt(POINTS_TO);
        return pointsTo.contains(BLOCK_POS) && pointsTo.contains(DIMENSION);
    }

    public static void setPointStrength(ItemStack compass, double strength) {
        GoodMC.LOGGER.debug("[CompassUtils] Setting compass point strength to: {}", strength);
        CompassUtils.getOrCreatePointsTo(compass).putDouble(STRENGTH, strength);
    }

    public static double getPointStrength(ItemStack compass) {
        if (!CompassUtils.isPointing(compass)) return -1;
        return CompassUtils.getPointsTo(compass).getDouble(STRENGTH);
    }

    public static boolean hasPointStrength(ItemStack compass) {
        return CompassUtils.isPointing(compass) && CompassUtils.getPointsTo(compass).contains(STRENGTH);
    }

    public static void setPointsToRandom(ItemStack compass, boolean random) {
        GoodMC.LOGGER.debug("[CompassUtils] Setting compass point random: {}", random);
        getOrCreatePointsTo(compass).putBoolean(RANDOM, random);
    }

    public static boolean isPointingRandom(ItemStack compass) {
        if (!isPointing(compass)) return false;
        return getPointsTo(compass).contains(RANDOM);
    }

    public static Boolean getRandom(ItemStack compass) {
        if (!isPointingRandom(compass)) return null;
        return getPointsTo(compass).getBoolean(RANDOM);
    }

}
