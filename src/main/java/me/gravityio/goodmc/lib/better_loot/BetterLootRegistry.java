package me.gravityio.goodmc.lib.better_loot;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.loot.LootTables.*;
import static net.minecraft.world.gen.structure.StructureKeys.*;

/**
 * A Registry that maps Loot Tables to Structures<br>
 * &nbsp;I'm not sure how else I can let a chest know which structure it's in, right now I just
 * check the lootable of the chest and use that loot table to map to a structure id using this, and then use that structure id to search for the {@link net.minecraft.structure.StructureStart StructureStart} at the
 * position of the chest with {@link net.minecraft.world.gen.StructureAccessor#getStructureAt(BlockPos, Structure) StructureAccessor#getStructureAt}
 */

public class BetterLootRegistry {
    public static final Identifier ALL = new Identifier("better_loot", "structures");
    /**
     * Maps Loot Table Identifiers -> Structures Identifiers<br>
     * When we want to find the Structure that a chest is within, <br>
     * we check the lootable of the chest and get the Structure Identifier from here
     */
    public static Map<Identifier, Identifier> lootTableStructure = new HashMap<>();

    /**
     * Maps Structure Identifiers -> List of Loot Table Identifiers<br>
     * When a {@link net.minecraft.block.entity.LootableContainerBlockEntity LootableContainerBlockEntity} gets looted it gets a list of all <br>
     * Loot Table Identifiers associated with its parent Structure and provides the loot tables items to the chest
     */
    public static Map<Identifier, List<Identifier>> structureLootTables = new HashMap<>();

    static {
        // Overworld
        registerStructure(VILLAGE_DESERT_HOUSE_CHEST, VILLAGE_DESERT.getValue());
        registerStructure(VILLAGE_PLAINS_CHEST, VILLAGE_PLAINS.getValue());
        registerStructure(VILLAGE_TAIGA_HOUSE_CHEST, VILLAGE_TAIGA.getValue());
        registerStructure(VILLAGE_SAVANNA_HOUSE_CHEST, VILLAGE_SAVANNA.getValue());
        registerStructure(VILLAGE_SNOWY_HOUSE_CHEST, VILLAGE_SNOWY.getValue());
        registerStructure(DESERT_PYRAMID_CHEST, DESERT_PYRAMID.getValue());
        registerStructure(WOODLAND_MANSION_CHEST, MANSION.getValue());
        registerStructure(PILLAGER_OUTPOST_CHEST, PILLAGER_OUTPOST.getValue());
        registerStructure(JUNGLE_PYRAMID.getValue(), new Identifier[] {
                JUNGLE_TEMPLE_CHEST, JUNGLE_TEMPLE_DISPENSER_CHEST
        });
        registerStructure(ANCIENT_CITY.getValue(), new Identifier[] {
                ANCIENT_CITY_CHEST, ANCIENT_CITY_ICE_BOX_CHEST
        });
        registerStructure(STRONGHOLD.getValue(), new Identifier[] {
                STRONGHOLD_CORRIDOR_CHEST, STRONGHOLD_CROSSING_CHEST, STRONGHOLD_LIBRARY_CHEST
        });
        registerStructure(BASTION_REMNANT.getValue(), new Identifier[] {
                BASTION_BRIDGE_CHEST, BASTION_OTHER_CHEST, BASTION_HOGLIN_STABLE_CHEST, BASTION_TREASURE_CHEST
        });
        // Nether
        registerStructure(NETHER_BRIDGE_CHEST, FORTRESS.getValue());
        // The End
        registerStructure(LootTables.END_CITY_TREASURE_CHEST, END_CITY.getValue());
    }

    public static void registerStructure(Identifier lootTableKey, Identifier structureKey) {
        GoodMC.LOGGER.debug("<BetterLootRegistry> Registering Structure {} from Loot Table: {}", structureKey, lootTableKey);
        lootTableStructure.put(lootTableKey, structureKey);
    }

    public static void registerStructure(Identifier structureKey, Identifier[] lootTables) {
        for (Identifier lootTable : lootTables) {
            registerStructure(lootTable, structureKey);
        }
    }

    public static void registerLoot(Identifier structureKey, Identifier lootTableKey) {
        GoodMC.LOGGER.debug("<BetterLootRegistry> Registering Loot {} for Structure: {}", lootTableKey, structureKey);
        if (!structureLootTables.containsKey(structureKey))
            structureLootTables.put(structureKey, new ArrayList<>());
        structureLootTables.get(structureKey).add(lootTableKey);
    }

    public static List<Identifier> getLoot(Identifier structureKey) {
        GoodMC.LOGGER.debug("<BetterLootRegistry> Getting Loot for Structure: {}", structureKey);
        List<Identifier> merged = new ArrayList<>();
        List<Identifier> all = structureLootTables.get(ALL);
        List<Identifier> structure = structureLootTables.get(structureKey);
        if (all != null)
            merged.addAll(all);
        if (structure != null)
            merged.addAll(structure);
        return merged;
    }

    public static Identifier getStructure(Identifier lootTable) {
        return lootTableStructure.get(lootTable);
    }
}
