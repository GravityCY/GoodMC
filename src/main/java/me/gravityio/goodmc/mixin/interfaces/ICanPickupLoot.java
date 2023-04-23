package me.gravityio.goodmc.mixin.interfaces;

public interface ICanPickupLoot {
    default boolean getCanPickupLoot() {
        return false;
    }

}
