package me.gravityio.goodmc.lib.better_compass;

import com.mojang.datafixers.util.Pair;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.tweaks.locator.LocatorTweak;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureLocatable implements IMovementLocatable {
    public static final String STRUCTURE_PATH = "structure";
    public static int UPDATE_DISTANCE = 100;
    public static int RADIUS = 1;


    /**
     * Returns whether an {@link ItemStack} contains the {@link StructureLocatable#STRUCTURE_PATH StructureLocatable#STRUCTURE } and {@link CompassUtils#DIMENSION CompassUtils#DIMENSION } NBT
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointing(ItemStack compass) {
        return CompassUtils.isPointingAtDimension(compass) && isPointingAtStructure(compass);
    }

    /**
     * Sets the compass to point to a structure in a dimension
     * @param compass
     * @param dimensionKey
     * @param structureKey
     */
    public static void setPointsTo(ItemStack compass, Identifier dimensionKey, Identifier structureKey) {
        CompassUtils.setPointDimension(compass, dimensionKey);
        setPointStructure(compass, structureKey);
        CompassUtils.setPointsToRandom(compass, true);
    }

    public static void setPointStructure(ItemStack compass, Identifier structureKey) {
        CompassUtils.getOrCreatePointsTo(compass).putString(STRUCTURE_PATH, structureKey.toString());
    }

    /**
     * Returns the {@link StructureLocatable#STRUCTURE_PATH StructureLocatable#STRUCTURE } NBT of an {@link ItemStack}
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static Identifier getPointStructure(ItemStack compass) {
        if (!isPointingAtStructure(compass)) return null;
        return new Identifier(CompassUtils.getPointsTo(compass).getString(STRUCTURE_PATH));
    }

    /**
     * Returns whether an {@link ItemStack} contains the {@link StructureLocatable#STRUCTURE_PATH StructureLocatable#STRUCTURE } NBT
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointingAtStructure(ItemStack compass) {
        return CompassUtils.isPointing(compass) && CompassUtils.getPointsTo(compass).contains(STRUCTURE_PATH);
    }

    public static boolean canLocate(ItemStack compass) {
        return compass.isOf(Items.COMPASS) && isPointing(compass);
    }

    /**
     * Updates the compass to the most recent structure that it's pointing at
     * @param compass {@link ItemStack}
     * @param serverWorld {@link ServerWorld}
     * @param serverPlayer {@link ServerPlayerEntity}
     */
    public static void updateLocator(ItemStack compass, ServerWorld serverWorld, ServerPlayerEntity serverPlayer) {
        if (!canLocate(compass)) return;

        Identifier dimensionKey = CompassUtils.getPointDimension(compass);
        Identifier structureKey = getPointStructure(compass);
        if (!serverWorld.getDimensionKey().getValue().equals(dimensionKey)) {
            GoodMC.LOGGER.debug("[StructureLocatable] Structure Dimension is not in current dimension");
            return;
        }
        long start = System.nanoTime();
        BlockPos playerPos = serverPlayer.getBlockPos();

        GoodMC.LOGGER.debug("[StructureLocatable] Looking for {}", structureKey);
        BlockPos locatedPos = locateStructure(serverWorld, structureKey, playerPos);
        GoodMC.LOGGER.debug("[StructureLocatable] Elapsed Time: {}ms", (System.nanoTime() - start) / 1000000L);
        if (locatedPos == null) return;
        GoodMC.LOGGER.debug("[StructureLocatable] Found structure at {}", locatedPos);

        double distLocated = Math.sqrt(playerPos.getSquaredDistance(locatedPos));
        if (!CompassUtils.isPointingAtPosition(compass)) {
            CompassUtils.setPointsToRandom(compass, false);
            serverPlayer.playSound(LocatorTweak.SOUND_STRUCTURE_LOCATED, SoundCategory.PLAYERS, 0.5f, 1);
        } else {
            double distPrev = Math.sqrt(playerPos.getSquaredDistance(CompassUtils.getPointPosition(compass)));
            if (distPrev < distLocated) {
                GoodMC.LOGGER.debug("[StructureLocatable] Previous position {}b is closer than the located position {}b, keeping previous...", distPrev, distLocated);
//                CompassUtils.setPointStrength(compass, Math.min(250d / distPrev, 1));
                return;
            }
        }

//        CompassUtils.setPointStrength(compass, Math.min(250d / distLocated, 1));
        CompassUtils.setPointPosition(compass, locatedPos);
        GoodMC.LOGGER.debug("[StructureLocatable] Setting the block position of found structure to {}", locatedPos);
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
        GoodMC.LOGGER.debug("[StructureLocatable] Locating structure starting from {} with a radius of {} in dimension {}", center, RADIUS, serverWorld.getRegistryKey().getValue().toString());
        Pair<BlockPos, RegistryEntry<Structure>> pair = serverWorld.getChunkManager().getChunkGenerator().locateStructure(serverWorld, registryEntryList, center, RADIUS, false);
        return pair != null ? pair.getFirst() : null;
    }

    @Override
    public boolean isLocatable(ItemStack compass, ServerPlayerEntity player) {
        return StructureLocatable.canLocate(compass);
    }

    @Override
    public void locate(ItemStack compass, ServerPlayerEntity player) {
        updateLocator(compass, player.getWorld(), player);
    }

    @Override
    public boolean hasMoved(double distance, double velocity) {
        return distance >= UPDATE_DISTANCE * (velocity + 1);
    }

    public static class StructureRegistry {
        private static final Map<Identifier, List<Identifier>> dimensionStructures = new HashMap<>();
        public static void registerStructure(Identifier dimensionKey, Identifier... structureKeys) {
            List<Identifier> structures = dimensionStructures.computeIfAbsent(dimensionKey, k -> new ArrayList<>());
            for (Identifier structureKey : structureKeys) {
                GoodMC.LOGGER.debug("[StructureRegistry] Registering structure:'{}' in dimension: '{}'", structureKey, dimensionKey);
                structures.add(structureKey);
            }
        }


        public static List<Identifier> getStructures(Identifier dimensionKey) {
            return dimensionStructures.get(dimensionKey);
        }
    }
}
