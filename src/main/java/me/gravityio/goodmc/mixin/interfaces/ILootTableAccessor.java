package me.gravityio.goodmc.mixin.interfaces;

import net.minecraft.util.Identifier;

/**
 * Anything that has and needs to provide a Loot table ID
 */
public interface ILootTableAccessor {
    Identifier getLootTable();
    void setLootTable(Identifier lootTable);
}
