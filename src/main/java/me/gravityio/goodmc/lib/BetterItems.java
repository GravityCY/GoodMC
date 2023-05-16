package me.gravityio.goodmc.lib;

import me.gravityio.goodmc.lib.helper.NbtUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

public class BetterItems {

    public static void setHotbarTooltip(ItemStack stack, Text text) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound display = NbtUtils.getOrCreate(nbt, ItemStack.DISPLAY_KEY);
        display.putString("hotbar", Text.Serializer.toJson(text));
    }

    public static Text getHotbarTooltip(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return null;
        NbtCompound display = NbtUtils.get(nbt, ItemStack.DISPLAY_KEY);
        if (display == null) return null;
        NbtString hotbarTooltip = NbtUtils.get(display, "hotbar", NbtString.class);
        if (hotbarTooltip == null) return null;
        return Text.Serializer.fromLenientJson(hotbarTooltip.asString());
    }

}
