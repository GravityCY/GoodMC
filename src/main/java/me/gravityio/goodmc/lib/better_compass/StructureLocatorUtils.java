package me.gravityio.goodmc.lib.better_compass;

import com.mojang.datafixers.util.Pair;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.tweaks.structure_locator.StructureLocatorTweak;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;

import static me.gravityio.goodmc.lib.better_compass.CompassUtils.DIMENSION;
import static me.gravityio.goodmc.lib.better_compass.CompassUtils.POINTS_TO;

/**
 * Some Helper functions to make the compass work nicely with finding structures etc.
 */
// Possible optimization idea is to disallow search for structures if the position of the new search is close to a previous search
// Meaning you have to keep a list of all previous search positions and for which structures and only for the current stack,
// if another stack with the same structure is present it would use a different list or something
public class StructureLocatorUtils {

    public static final String STRUCTURE = "structure";

    public static int RADIUS = 1;


    /**
     * Updates the compass to the most recent structure that it's pointing at
     * @param compass {@link ItemStack}
     * @param serverWorld {@link ServerWorld}
     * @param serverPlayer {@link ServerPlayerEntity}
     */
    public static void updateLocator(ItemStack compass, ServerWorld serverWorld, ServerPlayerEntity serverPlayer) {
        CompassLocatableRegistry.PointData pointData;
        if (!compass.getItem().equals(Items.COMPASS) || (pointData = CompassLocatableRegistry.PointData.fromItem(compass)) == null) return;
        if (!serverWorld.getDimensionKey().getValue().equals(pointData.dimensionKey())) {
            GoodMC.LOGGER.debug("<StructureLocatorUtils> Structure Dimension is not in current dimension");
            return;
        }
        GoodMC.LOGGER.debug("<StructureLocatorUtils> Looking for {}", pointData.structureKey());
        long start = System.nanoTime();
        BlockPos locatedPos = StructureLocatorUtils.locateStructure(serverWorld, pointData.structureKey(), serverPlayer.getBlockPos());
        if (locatedPos == null) return;
        if (!CompassUtils.isPointingAtPosition(compass)) {
            CompassUtils.setPointsToRandom(compass, false);
            serverPlayer.playSound(StructureLocatorTweak.LOCATED_SOUND, SoundCategory.PLAYERS, 0.5f, 1);
        }
        CompassUtils.setPointPosition(compass, locatedPos);
        GoodMC.LOGGER.debug("<StructureLocatorUtils> Setting the block position of found structure to {}", locatedPos);
        GoodMC.LOGGER.debug("<StructureLocatorUtils> Elapsed Time: {}ms", (System.nanoTime() - start) / 1000000L);
    }

    /**
     * Locates a structure
     * @param serverWorld {@link ServerWorld}
     * @param structureKey {@link Identifier}
     * @param center {@link BlockPos}
     * @return {@link BlockPos}
     */
    public static BlockPos locateStructure(ServerWorld serverWorld, Identifier structureKey, BlockPos center) {
        Registry<Structure> structureRegistry = serverWorld.getRegistryManager().get(RegistryKeys.STRUCTURE);
        Structure structure = structureRegistry.get(structureKey);
        RegistryEntry<Structure> registryEntry = structureRegistry.getEntry(structure);
        RegistryEntryList<Structure> registryEntryList = RegistryEntryList.of(registryEntry);
        GoodMC.LOGGER.debug("<StructureLocatorUtils> Locating structure starting from {} with a radius of {} in dimension {}", center, RADIUS, serverWorld.getRegistryKey().getValue().toString());
        Pair<BlockPos, RegistryEntry<Structure>> pair = serverWorld.getChunkManager().getChunkGenerator().locateStructure(serverWorld, registryEntryList, center, RADIUS, false);
        return pair != null ? pair.getFirst() : null;
    }

    /**
     * Returns whether an {@link ItemStack} is pointing at a specific structure
     * @param itemStack {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointingAtStructure(ItemStack itemStack) {
        return CompassUtils.isPointing(itemStack) && itemStack.getNbt().getCompound(POINTS_TO).contains(DIMENSION);
    }

    /**
     * Sets a compass to point to a specific structure in a specific dimension
     * @param compass {@link ItemStack}
     * @param pointData {@link CompassLocatableRegistry.PointData}
     */
    public static void setPointsTo(ItemStack compass, CompassLocatableRegistry.PointData pointData) {
        CompassUtils.setPointDimension(compass, pointData.dimensionKey());
        CompassUtils.setPointsToRandom(compass, true);
        NbtCompound pointsTo = CompassUtils.getPointsTo(compass);
        pointsTo.putString(STRUCTURE, pointData.structureKey().toString());
    }
}
