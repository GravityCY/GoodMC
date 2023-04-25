package me.gravityio.goodmc.mixin.mixins.better_shulkers;

import me.gravityio.goodmc.tweaks.better_shulkers.ShulkerUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackEnchantNBT {
    @Shadow
    public abstract NbtCompound getOrCreateSubNbt(String key);
    @Shadow
    public abstract @Nullable NbtCompound getNbt();

    @Shadow public abstract Text getName();

    @Inject(method = "addEnchantment", at = @At("TAIL"))
    public void addEnchantment(Enchantment enchantment, int level, CallbackInfo ci) {
        if (!ShulkerUtils.isShulker((ItemStack) (Object) this)) return;
        NbtCompound tag = getOrCreateSubNbt("BlockEntityTag");
        tag.put("Enchantments", Objects.requireNonNull(this.getNbt()).get("Enchantments"));
    }
}