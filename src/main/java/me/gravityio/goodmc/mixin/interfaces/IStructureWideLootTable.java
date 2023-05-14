package me.gravityio.goodmc.mixin.interfaces;

import net.minecraft.inventory.Inventory;

/**
 * Anything that gets provided with a loot table and needs to only spawn 1 of an item depending on the structure it is based in
 *
 */
public interface IStructureWideLootTable extends IStructureAccessor, ILootTableAccessor, Inventory {

}
