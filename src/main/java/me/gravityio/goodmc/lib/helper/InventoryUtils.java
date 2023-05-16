package me.gravityio.goodmc.lib.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

/**
 * Utility functions for Inventory Related Stuff
 */
public class InventoryUtils {

    public static boolean canInsertInventory(DefaultedList<Slot> slots, ItemStack original, int startIndex, int endIndex) {
        if (original.isEmpty()) return false;
        for (int i = startIndex; i < endIndex; i++) {
            Slot slot = slots.get(i);
            ItemStack other = slot.getStack();
            if (other.isEmpty() && slot.canInsert(original)) {
                return true;
            }
            if (original.isStackable()) {
                if (ItemStack.canCombine(original, other)) {
                    int total = other.getCount() + original.getCount();
                    if (total < original.getMaxCount()) return true;
                    else if (other.getCount() < original.getMaxCount()) return true;
                }
            }
        }
        return false;
    }

}
