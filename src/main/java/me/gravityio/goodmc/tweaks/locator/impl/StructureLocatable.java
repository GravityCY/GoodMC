package me.gravityio.goodmc.tweaks.locator.impl;

import com.mojang.datafixers.util.Pair;
import me.gravityio.goodlib.lib.BetterCompass;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureLocatable implements ILocatablePoi {
    public static final String STRUCTURE_PATH = "structure";
    public static final int CLOSE_RANGE = 256;
    public static int RADIUS = 6;

    /**
     * Returns whether an {@link ItemStack} contains the {@link StructureLocatable#STRUCTURE_PATH StructureLocatable#STRUCTURE } and {@link BetterCompass#DIMENSION BetterCompass#DIMENSION } NBT
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointing(ItemStack compass) {
        return BetterCompass.isPointingAtDimension(compass) && isPointingAtStructure(compass);
    }

    /**
     * Sets the compass to point to a structure in a dimension
     * @param compass Compass
     * @param dimensionKey Dimension
     * @param structureKey Structure
     */
    public static void setPointsTo(ItemStack compass, Identifier dimensionKey, Identifier structureKey) {
        BetterCompass.setPointDimension(compass, dimensionKey);
        setPointStructure(compass, structureKey);
        BetterCompass.setPointsToRandom(compass, true);
    }

    public static void setPointStructure(ItemStack compass, Identifier structureKey) {
        BetterCompass.getOrCreatePointsTo(compass).putString(STRUCTURE_PATH, structureKey.toString());
    }

    /**
     * Returns the {@link StructureLocatable#STRUCTURE_PATH StructureLocatable#STRUCTURE } NBT of an {@link ItemStack}
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static Identifier getPointStructure(ItemStack compass) {
        if (!isPointingAtStructure(compass)) return null;
        return new Identifier(BetterCompass.getPointsTo(compass).getString(STRUCTURE_PATH));
    }

    /**
     * Returns whether an {@link ItemStack} contains the {@link StructureLocatable#STRUCTURE_PATH StructureLocatable#STRUCTURE } NBT
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointingAtStructure(ItemStack compass) {
        return BetterCompass.isPointing(compass) && BetterCompass.getPointsTo(compass).contains(STRUCTURE_PATH);
    }

    public static boolean canLocate(ItemStack compass) {
        return compass.isOf(Items.COMPASS) && isPointing(compass);
    }

    @Override
    public Identifier getLocatableKey(ItemStack compass) {
        return StructureLocatable.getPointStructure(compass);
    }

    @Override
    public SoundEvent getSoundEvent() {
        return LocatorTweak.SOUND_STRUCTURE_LOCATED;
    }

    @Override
    public boolean isLocatable(ItemStack compass, ServerPlayerEntity player) {
        return StructureLocatable.canLocate(compass);
    }

    /**
     * Locates a structure
     * @param world {@link ServerWorld}
     * @param structureKey {@link Identifier}
     * @param center {@link BlockPos}
     * @return {@link BlockPos}
     */
    @Override
    public BlockPos locate(ServerWorld world, Identifier structureKey, BlockPos center) {
        Registry<Structure> structureRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
        Structure structure = structureRegistry.get(structureKey);
        RegistryEntry<Structure> registryEntry = structureRegistry.getEntry(structure);
        RegistryEntryList<Structure> registryEntryList = RegistryEntryList.of(registryEntry);
        GoodMC.LOGGER.debug("[StructureLocatable] Locating structure starting from {} with a radius of {} in dimension {}", center, RADIUS, world.getRegistryKey().getValue().toString());
        Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager().getChunkGenerator().locateStructure(world, registryEntryList, center, RADIUS, false);
        return pair != null ? pair.getFirst() : null;
    }

    @Override
    public boolean isDisallowedPosition(ServerPlayerEntity player, ItemStack compass, BlockPos prevPos, BlockPos locatedPos) {
        boolean isSuper = ILocatablePoi.super.isDisallowedPosition(player, compass, prevPos, locatedPos);
        if (isSuper)
            return true;

        boolean isWithin = prevPos.isWithinDistance(locatedPos, CLOSE_RANGE);
        if (isWithin)
            GoodMC.LOGGER.debug("[StructureLocatable] Located position is within {} blocks of the previous position!", CLOSE_RANGE);
        return isWithin;
    }

    public static class StructureRegistry {
        private static final Map<Identifier, List<Identifier>> dimensionStructures = new HashMap<>();
        public static void registerStructure(Identifier dimensionKey, Identifier... structureKeys) {
            List<Identifier> structures = dimensionStructures.computeIfAbsent(dimensionKey, k -> new ArrayList<>());
            for (Identifier structureKey : structureKeys) {
                if (structures.contains(structureKey)) {
                    GoodMC.LOGGER.debug("[StructureRegistry] Structure:'{}' in dimension: '{}', already exists skipping...", structureKey, dimensionKey);
                    continue;
                }
                GoodMC.LOGGER.debug("[StructureRegistry] Registering structure:'{}' in dimension: '{}'", structureKey, dimensionKey);
                structures.add(structureKey);
            }
        }

        public static Map<Identifier, List<Identifier>> getDimensionStructures() {
            return dimensionStructures;
        }

        public static List<Identifier> getStructures(Identifier dimensionKey) {
            return dimensionStructures.get(dimensionKey);
        }
        public static void clear() {
            GoodMC.LOGGER.debug("[StructureRegistry] Clearing Registry...");
            dimensionStructures.clear();
        }
    }
}
