package me.gravityio.goodmc.mixin.lib.client;

import me.gravityio.goodmc.lib.helper.NbtHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Adds support for custom hotbar tooltips <br><br>
 * -When you switch an Item in your hotbar it shows a tooltip of that item <br>
 * -This makes it so that if the Item has an NBT tag it will render that text instead of the display name of the item
 *
 */
@Debug(export = true)
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow private ItemStack currentStack;

    @ModifyVariable(
            method = "renderHeldItemTooltip",
            at = @At(value = "STORE"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isEmpty ()Z")
            ),
            ordinal = 0
    )
    private MutableText onRenderCustomTooltip(MutableText original) {
        NbtCompound nbt = this.currentStack.getNbt();
        if (nbt == null) return original;
        NbtCompound display = NbtHelper.get(nbt, ItemStack.DISPLAY_KEY);
        if (display == null) return original;
        NbtString hotbarTooltip = NbtHelper.get(display, "hotbar", NbtString.class);
        if (hotbarTooltip == null) return original;
        this.currentStack.getNbt().get(ItemStack.DISPLAY_KEY);
        return Text.Serializer.fromLenientJson(hotbarTooltip.asString());
    }
}
