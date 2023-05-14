package me.gravityio.goodmc.mixin.interfaces;

/**
 * For the SpawnPacket of entity to also have if they can pickup loot
 */
public interface ILootEntity {
    default boolean getCanPickupLoot() {
        return false;
    }

}
