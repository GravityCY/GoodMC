package me.gravityio.goodmc.lib.better_loot;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Registry that maps Loot Tables to Structures<br>
 * &nbsp;I'm not sure how else I can let a chest know which structure it's in, right now I just
 * check the lootable of the chest and use that loot table to map to a structure id using this, and then use that structure id to search for the {@link net.minecraft.structure.StructureStart StructureStart} at the
 * position of the chest with {@link net.minecraft.world.gen.StructureAccessor#getStructureAt(BlockPos, Structure) StructureAccessor#getStructureAt}
 */

public class BetterLootRegistry {
    public static final Identifier ALL = new Identifier("better_loot", "structures");

    /**
     * Maps Structure Identifiers -> List of Loot Table Identifiers<br>
     * When a {@link net.minecraft.block.entity.LootableContainerBlockEntity LootableContainerBlockEntity} gets looted it gets a list of all <br>
     * Loot Table Identifiers associated with its parent Structure and provides the loot tables items to the chest
     */
    public static Map<Identifier, List<Identifier>> structureLootTables = new HashMap<>();


    public static void registerLoot(Identifier structureKey, Identifier lootTableKey) {
        GoodMC.LOGGER.debug("[BetterLootRegistry] Registering Loot {} for Structure: {}", lootTableKey, structureKey);
        if (!structureLootTables.containsKey(structureKey))
            structureLootTables.put(structureKey, new ArrayList<>());
        structureLootTables.get(structureKey).add(lootTableKey);
    }

    public static List<Identifier> getLoot(Identifier structureKey) {
        GoodMC.LOGGER.debug("[BetterLootRegistry] Getting Loot for Structure: {}", structureKey);
        List<Identifier> merged = new ArrayList<>();
        List<Identifier> all = structureLootTables.get(ALL);
        List<Identifier> structure = structureLootTables.get(structureKey);
        if (all != null)
            merged.addAll(all);
        if (structure != null)
            merged.addAll(structure);
        return merged;
    }

}
