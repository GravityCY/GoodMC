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

import java.util.*;

import static me.gravityio.goodmc.lib.better_compass.CompassUtils.DIMENSION;
import static me.gravityio.goodmc.lib.better_compass.CompassUtils.POINTS_TO;
import static me.gravityio.goodmc.lib.better_compass.StructureLocatorUtils.STRUCTURE;

public class StructureLocatable implements IMovementLocatable {
    private static final Map<Identifier, List<Identifier>> dimensionStructures = new HashMap<>();

    public static final int UPDATE_DISTANCE = 100;
    public static final int RADIUS = 100;


    public static void registerStructure(Identifier dimensionKey, Identifier structureKey) {
        List<Identifier> structures = dimensionStructures.computeIfAbsent(dimensionKey, k -> new ArrayList<>());
        GoodMC.LOGGER.debug("<CompassLocatableRegistry> Registering structure:'{}' in dimension: '{}'", structureKey, dimensionKey);
        structures.add(structureKey);
    }

    public static void registerStructure(Identifier dimensionKey, Identifier[] structureKeys) {
        for (Identifier structureKey : structureKeys)
            registerStructure(dimensionKey, structureKey);
    }

    public static List<Identifier> getStructure(Identifier dimensionKey) {
        return dimensionStructures.get(dimensionKey);
    }

    @Override
    public boolean isLocatable(ItemStack compass, ServerPlayerEntity player) {
        if (!compass.getItem().equals(Items.COMPASS)) return false;
        return false;
    }

    @Override
    public BlockPos locate(ItemStack compass, ServerPlayerEntity player) {
        ServerWorld serverWorld = player.getWorld();
        CompassLocatableRegistry.PointData pointData;
        if (!compass.getItem().equals(Items.COMPASS) || (pointData = CompassLocatableRegistry.PointData.fromItem(compass)) == null) return;
        if (!serverWorld.getDimensionKey().getValue().equals(pointData.dimensionKey())) {
            GoodMC.LOGGER.debug("<StructureLocatorUtils> Structure Dimension is not in current dimension");
            return null;
        }
        GoodMC.LOGGER.debug("<StructureLocatorUtils> Looking for {}", pointData.structureKey());
        long start = System.nanoTime();
        BlockPos locatedPos = StructureLocatorUtils.locateStructure(serverWorld, pointData.structureKey(), player.getBlockPos());
        if (locatedPos == null) return null;
        if (!CompassUtils.isPointingAtPosition(compass)) {
            CompassUtils.setPointsToRandom(compass, false);
            player.playSound(StructureLocatorTweak.LOCATED_SOUND, SoundCategory.PLAYERS, 0.5f, 1);
        }
        GoodMC.LOGGER.debug("<StructureLocatorUtils> Setting the block position of found structure to {}", locatedPos);
        GoodMC.LOGGER.debug("<StructureLocatorUtils> Elapsed Time: {}ms", (System.nanoTime() - start) / 1000000L);
        return locatedPos;
    }

    private static BlockPos locateStructure(ServerWorld serverWorld, Identifier structureKey, BlockPos center) {
        Registry<Structure> structureRegistry = serverWorld.getRegistryManager().get(RegistryKeys.STRUCTURE);
        Structure structure = structureRegistry.get(structureKey);
        RegistryEntry<Structure> registryEntry = structureRegistry.getEntry(structure);
        RegistryEntryList<Structure> registryEntryList = RegistryEntryList.of(registryEntry);
        GoodMC.LOGGER.debug("<StructureLocatorUtils> Locating structure starting from {} with a radius of {} in dimension {}", center, RADIUS, serverWorld.getRegistryKey().getValue().toString());
        Pair<BlockPos, RegistryEntry<Structure>> pair = serverWorld.getChunkManager().getChunkGenerator().locateStructure(serverWorld, registryEntryList, center, RADIUS, false);
        return pair != null ? pair.getFirst() : null;
    }

    @Override
    public int getUpdateDistance() {
        return UPDATE_DISTANCE;
    }

    public record PointData(Identifier dimensionKey, Identifier structureKey) {
        public static PointData fromNbt(NbtCompound nbt) {
            String dimension = nbt.getString(DIMENSION);
            if (Objects.equals(dimension, "")) return null;
            String structure = nbt.getString(STRUCTURE);
            if (Objects.equals(structure, "")) return null;
            return new PointData(new Identifier(dimension), new Identifier(structure));
        }

        public static PointData fromItem(ItemStack itemStack) {
            NbtCompound nbt = itemStack.getNbt();
            if (nbt == null) return null;
            NbtCompound pointsTo = nbt.getCompound(POINTS_TO);
            if (pointsTo == null) return null;
            return fromNbt(pointsTo);
        }

        public static NbtCompound toNbt(PointData data) {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("dimension", data.dimensionKey.toString());
            nbt.putString("structure", data.structureKey.toString());
            return nbt;
        }

    }
}
