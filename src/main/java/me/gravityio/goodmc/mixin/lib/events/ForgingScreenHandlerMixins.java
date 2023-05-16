package me.gravityio.goodmc.mixin.lib.events;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.lib.events.ModEvents;
import me.gravityio.goodmc.lib.helper.InventoryUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds an event for when something is crafted in the smithing table
 */
public abstract class ForgingScreenHandlerMixins  {

    @Mixin(ForgingScreenHandler.class)
    private abstract static class ForgingScreenHandlerMixin extends ScreenHandler {
        protected ForgingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
            super(type, syncId);
        }
        @Inject(method = "quickMove",
                at = @At(value = "INVOKE", target = "net/minecraft/screen/slot/Slot.getStack ()Lnet/minecraft/item/ItemStack;", ordinal = 0))
        private void onBeforeCraft(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
            if (slot != 2 || !InventoryUtils.canInsertInventory(this.slots, this.slots.get(slot).getStack(), 3, 39)) return;
            ForgingScreenHandler self = (ForgingScreenHandler) (Object) this;
            if (self instanceof SmithingScreenHandler)
                ModEvents.ON_BEFORE_CRAFT.invoker().onBeforeCraft(ModEvents.OnCraftEvent.CraftType.SMITHING, this.slots.get(slot).getStack(), player);
        }
    }

    @Mixin(SmithingScreenHandler.class)
    private static class SmithingScreenHandlerMixin {
        @Inject(method = "onTakeOutput", at = @At("HEAD"))
        private void onCraft(PlayerEntity player, ItemStack stack, CallbackInfo info) {
            if (stack.isEmpty()) return;
            GoodMC.LOGGER.debug("<SmithingScreenHandlerMixin> Player {} crafted item {} in smithing table", player.getDisplayName().getString(), stack.getName());
            ModEvents.ON_CRAFT.invoker().onCraft(ModEvents.OnCraftEvent.CraftType.SMITHING, stack, player);
        }
    }
}
