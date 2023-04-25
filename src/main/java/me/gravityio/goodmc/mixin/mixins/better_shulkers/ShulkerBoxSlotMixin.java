package me.gravityio.goodmc.mixin.mixins.better_shulkers;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxSlot.class)
public abstract class ShulkerBoxSlotMixin extends Slot {
    @Shadow public abstract boolean canInsert(ItemStack stack);

    public ShulkerBoxSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }
    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(this.inventory instanceof SidedInventory)) return;
        cir.setReturnValue(((SidedInventory)this.inventory).canInsert(0, stack, null));
    }
}