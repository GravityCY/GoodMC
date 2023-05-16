package me.gravityio.goodmc.lib.better_compass;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class BiomeLocatable implements ILocatable{
    @Override
    public boolean isLocatable(ItemStack compass, ServerPlayerEntity player) {
        return false;
    }

    @Override
    public BlockPos locate(ItemStack compass, ServerPlayerEntity player) {
        return null;
    }
}
