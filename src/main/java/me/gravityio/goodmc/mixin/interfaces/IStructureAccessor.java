package me.gravityio.goodmc.mixin.interfaces;


import me.gravityio.goodmc.tweaks.structure_locator.LootedStructuresState;

/**
 * 
 */
public interface IStructureAccessor {
    LootedStructuresState.LootableStructure getStructure();
    void setStructure(LootedStructuresState.LootableStructure start);
}
