package me.gravityio.goodmc.lib.helper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class EnchantmentUtils {

    public static final String ID_KEY = "id";
    public static final String LEVEL_KEY = "lvl";

    public static boolean hasEnchantment(Enchantment enchantment, NbtList nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            if (isEnchantment(nbt.getCompound(i), enchantment)) return true;
        }
        return false;
    }

    public static boolean hasEnchantment(Identifier enchantmentKey, NbtList nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            if (isEnchantment(nbt.getCompound(i), enchantmentKey)) return true;
        }
        return false;
    }

    public static boolean hasEnchantment(String enchantmentId, NbtList nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            if (isEnchantment(nbt.getCompound(i), enchantmentId)) return true;
        }
        return false;
    }

    private static boolean isEnchantment(NbtCompound nbt, Enchantment enchantment) {
        if (!nbt.contains(ID_KEY)) return false;
        return Objects.equals(Registries.ENCHANTMENT.get(new Identifier(nbt.getString(ID_KEY))), enchantment);
    }

    private static boolean isEnchantment(NbtCompound nbt, Identifier id) {
        return new Identifier(nbt.getString(ID_KEY)).equals(id);
    }

    private static boolean isEnchantment(NbtCompound nbt, String id) {
        return nbt.getString(ID_KEY).equals(id);
    }


}
