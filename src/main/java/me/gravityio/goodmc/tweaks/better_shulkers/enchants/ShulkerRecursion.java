package me.gravityio.goodmc.tweaks.better_shulkers.enchants;

import me.gravityio.goodmc.tweaks.better_shulkers.ShulkerUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * An Enchant that allows for Shulkers to be placed into Shulkers
 */
@SuppressWarnings("unused")
public class ShulkerRecursion extends Enchantment {

    public ShulkerRecursion() {
        super(Rarity.VERY_RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND });
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }
    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return ShulkerUtils.isShulker(stack);
    }
}
