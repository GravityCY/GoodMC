package me.gravityio.goodmc.tweaks.better_shulkers;

import me.gravityio.goodmc.random.ItemInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Supplier;


/**
 * An Item with an inventory that's inside a Container / Inventory <br><br>
 * Makes it possible to check if the parent inventory is still capable of being opened and if the item inventory is still at the same slot location<br><br>
 * It uses a Boolean Supplier with preferably the screen handler of the parent's {@link net.minecraft.screen.ScreenHandler#canUse(PlayerEntity) Screenhandler#canUse(PlayerEntity)} function as a Supplier <br><br>
 * And the Clicked {@link Slot} of the parent's inventory to use to check if the item is still in the inventory
 */
// TODO: Need to somehow synchronize the inventories for multiplayer
//    Or maybe just disallow players to open a shulker if the inventory is already opened by someone else
public class ContainedItemInventory extends ItemInventory {
    private final Supplier<Boolean> parentCanPlayerUseSupplier;
    private final Slot parentInventoryStackSlot;

    public ContainedItemInventory(int size, ItemStack inventoryStack, Slot parentInventoryStackSlot, Supplier<Boolean> parentCanPlayerUseSupplier) {
        super(inventoryStack, size);
        this.parentCanPlayerUseSupplier = parentCanPlayerUseSupplier;
        this.parentInventoryStackSlot = parentInventoryStackSlot;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return parentCanPlayerUseSupplier.get() && parentInventoryStackSlot.getStack().equals(inventoryStack) && !inventoryStack.isEmpty();
    }
}
