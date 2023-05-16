package me.gravityio.goodmc.lib.better_compass;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.*;

import static me.gravityio.goodmc.lib.better_compass.CompassUtils.DIMENSION;
import static me.gravityio.goodmc.lib.better_compass.CompassUtils.POINTS_TO;
import static me.gravityio.goodmc.lib.better_compass.StructureLocatorUtils.STRUCTURE;

/**
 * A Registry containing structures that compasses can use to point towards
 */
public class CompassLocatableRegistry {
    private static final Map<Identifier, List<Identifier>> dimensionStructures = new HashMap<>();
    private static final Map<Identifier, List<Identifier>> dimensionBiomes = new HashMap<>();

    public static void registerStructure(Identifier dimensionKey, Identifier structureKey) {
        List<Identifier> structures = dimensionStructures.computeIfAbsent(dimensionKey, k -> new ArrayList<>());
        GoodMC.LOGGER.debug("<CompassLocatableRegistry> Registering structure:'{}' in dimension: '{}'", structureKey, dimensionKey);
        structures.add(structureKey);
    }

    public static void registerStructure(Identifier dimensionKey, Identifier[] structureKeys) {
        for (Identifier structureKey : structureKeys)
            registerStructure(dimensionKey, structureKey);
    }

    public static List<Identifier> getStructures(Identifier dimensionKey) {
        return dimensionStructures.get(dimensionKey);
    }

    public record PointData(Identifier dimensionKey, Identifier structureKey) {
        public static StructureLocatable.PointData fromNbt(NbtCompound nbt) {
            String dimension = nbt.getString(DIMENSION);
            if (Objects.equals(dimension, "")) return null;
            String structure = nbt.getString(STRUCTURE);
            if (Objects.equals(structure, "")) return null;
            return new StructureLocatable.PointData(new Identifier(dimension), new Identifier(structure));
        }

        public static StructureLocatable.PointData fromItem(ItemStack itemStack) {
            NbtCompound nbt = itemStack.getNbt();
            if (nbt == null) return null;
            NbtCompound pointsTo = nbt.getCompound(POINTS_TO);
            if (pointsTo == null) return null;
            return fromNbt(pointsTo);
        }

        public static NbtCompound toNbt(StructureLocatable.PointData data) {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("dimension", data.dimensionKey.toString());
            nbt.putString("structure", data.structureKey.toString());
            return nbt;
        }

    }
}
