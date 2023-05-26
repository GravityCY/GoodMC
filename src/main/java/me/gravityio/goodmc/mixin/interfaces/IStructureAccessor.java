package me.gravityio.goodmc.mixin.interfaces;


import me.gravityio.goodmc.tweaks.locator.LootedStructuresState;

/**
 * 
 */
public interface IStructureAccessor {
    LootedStructuresState.LootableStructure getStructure();
    void setStructure(LootedStructuresState.LootableStructure start);
}
