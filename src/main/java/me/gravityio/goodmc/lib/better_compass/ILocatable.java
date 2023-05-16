package me.gravityio.goodmc.lib.better_compass;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface ILocatable {
    boolean isLocatable(ItemStack compass, ServerPlayerEntity player);
    BlockPos locate(ItemStack compass, ServerPlayerEntity player);
}
