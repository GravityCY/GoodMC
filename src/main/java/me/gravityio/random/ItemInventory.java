package me.gravityio.random;

import me.gravityio.goodmc.tweaks.better_shulkers.ShulkerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

import java.util.function.Function;


/**
 *      An Inventory based off of an ItemStack, for example Shulker Boxes <br>
 *      Gives the ability to proxy all Inventory functions to the NBT data of the ItemStack
 */
@SuppressWarnings("ALL")
public class ItemInventory extends SimpleInventory {

    protected final ItemStack inventoryStack;
    protected final NbtInventory nbtInventory;
    protected final Function<PlayerEntity, Boolean> canPlayerUse;


    /**
     * @param inventoryStack The Stack the inventory is going to based off
     * @param size The size of the inventory
     * @param canPlayerUse A Function that will determine whether the inventory should close
     */
    public ItemInventory(ItemStack inventoryStack, int size, Function<PlayerEntity, Boolean> canPlayerUse) {
        super(size);
        this.inventoryStack = inventoryStack;
        this.nbtInventory = new NbtInventory(NbtInventory.getNbtInventory(inventoryStack));
        this.canPlayerUse = canPlayerUse;
        ShulkerUtils.getOrderedInventory(inventoryStack).forEach(super.stacks::set);
    }
    @Override
    public void setStack(int slot, ItemStack stack) {
        nbtInventory.setStack(slot, stack);
        super.setStack(slot, stack);
    }
    @Override
    public ItemStack removeStack(int slot, int amount) {
        nbtInventory.removeStack(slot, amount);
        return super.removeStack(slot, amount);
    }
    @Override
    public ItemStack removeStack(int slot) {
        nbtInventory.removeStack(slot);
        return super.removeStack(slot);
    }
    @Override
    public void clear() {
        nbtInventory.clear();
        super.clear();
    }
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.canPlayerUse != null ? this.canPlayerUse.apply(player) : true;
    }
}
