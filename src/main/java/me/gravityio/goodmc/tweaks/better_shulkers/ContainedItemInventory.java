package me.gravityio.goodmc.tweaks.better_shulkers;

import me.gravityio.goodmc.lib.item_inventory.ItemInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


/**
 * An Item with an inventory that's inside a Container / Inventory <br><br>
 * Makes it possible to check if the parent inventory is still capable of being opened and if the item inventory is still at the same slot location<br><br>
 * It uses a Boolean Supplier with preferably the screen handler of the parent's {@link net.minecraft.screen.ScreenHandler#canUse(PlayerEntity) Screenhandler#canUse(PlayerEntity)} function as a Supplier <br><br>
 * And the Clicked {@link Slot} of the parent's inventory to use to check if the item is still in the inventory
 */
public class ContainedItemInventory extends ItemInventory {
    private static final Map<ItemStack, ContainedItemInventory> inventoryMap = new HashMap<>();
    private int viewers = 0;
    private final Supplier<Boolean> parentCanPlayerUseSupplier;
    private final Slot parentInventoryStackSlot;

    public static ContainedItemInventory getFromStack(ItemStack stack) {
        return inventoryMap.get(stack);
    }

    public static ContainedItemInventory make(int size, ItemStack inventoryStack, Slot parentInventoryStackSlot, Supplier<Boolean> parentCanPlayerUseSupplier) {
        ContainedItemInventory inventory = new ContainedItemInventory(size, inventoryStack, parentInventoryStackSlot, parentCanPlayerUseSupplier);
        inventoryMap.put(inventoryStack, inventory);
        return inventory;
    }

    public ContainedItemInventory(int size, ItemStack inventoryStack, Slot parentInventoryStackSlot, Supplier<Boolean> parentCanPlayerUseSupplier) {
        super(inventoryStack, size);
        this.parentCanPlayerUseSupplier = parentCanPlayerUseSupplier;
        this.parentInventoryStackSlot = parentInventoryStackSlot;
    }

    @Override
    public void markDirty() {
        parentInventoryStackSlot.inventory.markDirty();
    }

    @Override
    public void onOpen(PlayerEntity player) {
        viewers++;
    }

    @Override
    public void onClose(PlayerEntity player) {
        viewers--;
        if (viewers != 0) return;
        inventoryMap.remove(inventoryStack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return parentCanPlayerUseSupplier.get() && parentInventoryStackSlot.getStack().equals(inventoryStack) && !inventoryStack.isEmpty();
    }
}
