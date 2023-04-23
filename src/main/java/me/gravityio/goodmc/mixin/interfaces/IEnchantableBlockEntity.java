package me.gravityio.goodmc.mixin.interfaces;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public interface IEnchantableBlockEntity {
    NbtList getEnchantments();
    void setEnchantments(NbtList nbtList);
    default NbtCompound toClientNbt() {
        // Only send the enchantments to the client to reduce packet size
        NbtCompound nbt = new NbtCompound();
        nbt.put("Enchantments", getEnchantments());
        return nbt;
    }
}
