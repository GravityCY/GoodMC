package me.gravityio.goodmc.mixin.interfaces;

import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public interface ILocatorPlayer {

    /**
     * Gets all available structures for this player <br>
     * Structures already been rolled will not be in this list, but in the excluded one
     * @return
     */
    Map<Identifier, List<Identifier>> getAvailableStructures();
    void setAvailableStructures(Map<Identifier, List<Identifier>> availableStructures);

    Map<Identifier, List<Identifier>> getAvailableBiomes();
    void setAvailableBiomes(Map<Identifier, List<Identifier>> availableBiomes);

    Map<Identifier, List<Identifier>> getExcludedBiomes();
    void setExcludedBiomes(Map<Identifier, List<Identifier>> excludedBiomes);

    Map<Identifier, List<Identifier>> getExcludedStructures();
    void setExcludedStructures(Map<Identifier, List<Identifier>> excludedStructures);

    public default void addExcludedStructure(Identifier dimension, Identifier structure) {
        getAvailableStructures().get(dimension).remove(structure);
        getExcludedStructures().get(dimension).add(structure);
    }
    public default void addExcludedBiome(Identifier dimension, Identifier biome) {
        getAvailableBiomes().get(dimension).remove(biome);
        getExcludedBiomes().get(dimension).add(biome);
    }


}
