package me.gravityio.goodmc.tweaks.better_shulkers;

import me.gravityio.random.ItemInventory;
import me.gravityio.random.NbtUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;


/**
 * Kind of a helper Class because everytime you instantiated ItemInventory you needed to check whether the block it was based on was broken
 * TODO: Need to somehow synchronize the inventories for multiplayer
 */
public class ShulkerItemInventory extends ItemInventory {
    private final int slot;
    private final BlockPos pos;
    public ShulkerItemInventory(ItemStack inventoryStack, int size, int slot) {
        super(inventoryStack, size, null);
        assert inventoryStack.getNbt() != null;
        this.slot = slot;
        this.pos = NbtUtils.fromNbt(inventoryStack.getNbt().getCompound("ParentPos"));
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
        return blockEntity != null && !inventoryStack.isEmpty() && ItemStack.areEqual(((LootableContainerBlockEntity)blockEntity).getStack(slot), inventoryStack);
    }
}
