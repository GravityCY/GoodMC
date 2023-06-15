package me.gravityio.goodmc.tweaks.locator.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ILocatable{
    /**
     * This should return a boolean if the passed item is of the current locatable, determines whether to execute the {@link ILocatable#locate ILocatable#locate} function
     * @param compass The could-be locatable item
     * @param player The player
     * @return {@link Boolean boolean}
     */
    boolean isLocatable(ItemStack compass, ServerPlayerEntity player);
    boolean locate(ItemStack compass, ServerPlayerEntity player);

}
