package me.gravityio.goodmc.tweaks.better_shulkers.enchants;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

/**
 * An Enchant that gives you immunity to shulker bullets, negating the Levitation Effect only (damage still remains)
 */
@SuppressWarnings("unused")
public class ShulkerAffinity extends Enchantment {

    public ShulkerAffinity() {
        super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR, new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET });
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }
    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }
}
