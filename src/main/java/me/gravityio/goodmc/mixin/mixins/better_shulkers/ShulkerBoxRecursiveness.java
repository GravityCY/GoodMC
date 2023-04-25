package me.gravityio.goodmc.mixin.mixins.better_shulkers;

import me.gravityio.goodmc.mixin.interfaces.IEnchantableBlockEntity;
import me.gravityio.goodmc.tweaks.better_shulkers.ShulkerUtils;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxRecursiveness {
    @Inject(method="canInsert", at = @At("HEAD"), cancellable = true)
    private void canInsertInto(int slot, ItemStack stack, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ShulkerUtils.canInsert(stack, (IEnchantableBlockEntity) this));
    }

}