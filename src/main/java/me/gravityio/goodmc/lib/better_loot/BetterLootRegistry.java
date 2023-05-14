package me.gravityio.goodmc.lib.better_loot;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.HashMap;
import java.util.Map;

/**
 * A Registry that maps Loot Tables to Structures<br>
 * &nbsp;I'm not sure how else I can let a chest know which structure it's in, right now I just
 * check the lootable of the chest and use that loot table to map to a structure using this, and then use that structure to search for the {@link net.minecraft.structure.StructureStart StructureStart} at the
 * position of the chest with {@link net.minecraft.world.gen.StructureAccessor#getStructureAt(BlockPos, Structure) StructureAccessor#getStructureAt}
 */

public class BetterLootRegistry {
    public static Map<Identifier, Identifier> lootTableStructure = new HashMap<>();

    static {
        // Overworld
        BetterLootRegistry.register(LootTables.DESERT_PYRAMID_CHEST, StructureKeys.DESERT_PYRAMID.getValue());
        BetterLootRegistry.register(LootTables.JUNGLE_TEMPLE_CHEST, StructureKeys.JUNGLE_PYRAMID.getValue());
        BetterLootRegistry.register(LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST, StructureKeys.JUNGLE_PYRAMID.getValue());
        BetterLootRegistry.register(LootTables.ANCIENT_CITY_CHEST, StructureKeys.ANCIENT_CITY.getValue());
        BetterLootRegistry.register(LootTables.ANCIENT_CITY_ICE_BOX_CHEST, StructureKeys.ANCIENT_CITY.getValue());
        BetterLootRegistry.register(LootTables.STRONGHOLD_CORRIDOR_CHEST, StructureKeys.STRONGHOLD.getValue());
        BetterLootRegistry.register(LootTables.STRONGHOLD_CROSSING_CHEST, StructureKeys.STRONGHOLD.getValue());
        BetterLootRegistry.register(LootTables.STRONGHOLD_LIBRARY_CHEST, StructureKeys.STRONGHOLD.getValue());
        BetterLootRegistry.register(LootTables.WOODLAND_MANSION_CHEST, StructureKeys.MANSION.getValue());
        BetterLootRegistry.register(LootTables.PILLAGER_OUTPOST_CHEST, StructureKeys.PILLAGER_OUTPOST.getValue());
        // Nether
        BetterLootRegistry.register(LootTables.NETHER_BRIDGE_CHEST, StructureKeys.FORTRESS.getValue());
        BetterLootRegistry.register(LootTables.BASTION_BRIDGE_CHEST, StructureKeys.BASTION_REMNANT.getValue());
        BetterLootRegistry.register(LootTables.BASTION_OTHER_CHEST, StructureKeys.BASTION_REMNANT.getValue());
        BetterLootRegistry.register(LootTables.BASTION_HOGLIN_STABLE_CHEST, StructureKeys.BASTION_REMNANT.getValue());
        BetterLootRegistry.register(LootTables.BASTION_TREASURE_CHEST, StructureKeys.BASTION_REMNANT.getValue());
        // The End
        BetterLootRegistry.register(LootTables.END_CITY_TREASURE_CHEST, StructureKeys.END_CITY.getValue());
    }

    public static void register(Identifier lootTableKey, Identifier structureKey) {
        GoodMC.LOGGER.debug("<BetterLootRegistry> Registering Loot Table ({}) for Structure: {}", lootTableKey, structureKey);
        lootTableStructure.put(lootTableKey, structureKey);
    }
    public static Identifier get(Identifier lootTable) {
        return lootTableStructure.get(lootTable);
    }
}
