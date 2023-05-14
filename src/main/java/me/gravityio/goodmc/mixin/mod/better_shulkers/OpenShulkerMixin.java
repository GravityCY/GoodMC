package me.gravityio.goodmc.mixin.mod.better_shulkers;

import me.gravityio.goodmc.lib.TriFunction;
import me.gravityio.goodmc.tweaks.better_shulkers.BetterShulkersRegistry;
import me.gravityio.goodmc.tweaks.better_shulkers.ShulkerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ScreenHandler.class)
public abstract class OpenShulkerMixin {

    private static final TriFunction<ItemStack, Slot, Supplier<Boolean>, NamedScreenHandlerFactory> defScreenHandler = BetterShulkersRegistry.getScreenHandler(Items.SHULKER_BOX);
    @Shadow @Final public DefaultedList<Slot> slots;
    @Shadow @Final private @Nullable ScreenHandlerType<?> type;
    @Shadow public abstract boolean canUse(PlayerEntity var1);

    @Inject(method="onSlotClick", at = @At("HEAD"), cancellable = true)
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity) ||button != GLFW.GLFW_MOUSE_BUTTON_2 || this.type == null || !BetterShulkersRegistry.isAllowedScreen(this.type)) return;
        int size = this.slots.size();
        ItemStack stack = slotIndex >= 0 && slotIndex < size - player.getInventory().main.size() ? this.slots.get(slotIndex).getStack() : ItemStack.EMPTY;
        if (stack.isEmpty() || !ShulkerUtils.isShulker(stack)) return;
        TriFunction<ItemStack, Slot, Supplier<Boolean>, NamedScreenHandlerFactory> onOpen = BetterShulkersRegistry.getScreenHandler(stack.getItem());
        if (onOpen == null) onOpen = defScreenHandler;

        Supplier<Boolean> booleanSupplier = () -> this.canUse(player);

        player.openHandledScreen(onOpen.apply(stack, this.slots.get(slotIndex), booleanSupplier));
        ci.cancel();
    }

}