package me.gravityio.goodmc.lib.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A class with some static methods that should have been in the {@link ItemStack} class
 */
public class ItemUtils {

    public static void setLore(ItemStack stack, Text[] loreInput) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound display = NbtUtils.getOrCreate(nbt, ItemStack.DISPLAY_KEY);
        NbtList loreList = (NbtList) NbtUtils.getOrCreate(display, ItemStack.LORE_KEY, NbtElement.LIST_TYPE);
        loreList.clear();
        for (Text loreLine : loreInput)
            loreList.add(NbtString.of(Text.Serializer.toJson(loreLine)));
    }

    public static List<String> getLore(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return null;
        NbtCompound display = NbtUtils.get(nbt, ItemStack.DISPLAY_KEY, NbtCompound.class);
        if (display == null) return null;
        NbtList loreList = NbtUtils.get(display, ItemStack.LORE_KEY, NbtList.class);
        if (loreList == null) return null;
        List<String> loreArray = new ArrayList<>();
        for (NbtElement element : loreList)
            loreArray.add(element.asString());
        return loreArray;
    }

    public static String getLoreAsString(ItemStack stack) {
        List<String> lore = getLore(stack);
        if (lore == null) return null;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            stringBuilder.append(line);
            if (i != lore.size() - 1) stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static void setHotbarTooltip(ItemStack stack, Text text) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound display = NbtUtils.getOrCreate(nbt, ItemStack.DISPLAY_KEY);
        display.putString("hotbar", Text.Serializer.toJson(text));
    }

}
