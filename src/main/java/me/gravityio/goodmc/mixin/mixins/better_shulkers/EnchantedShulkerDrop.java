package me.gravityio.goodmc.mixin.mixins.better_shulkers;

import me.gravityio.goodmc.mixin.interfaces.IEnchantableBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractBlock.class)
public class EnchantedShulkerDrop {
    @Inject(method = "getDroppedStacks", at = @At("RETURN"))
    public void getDropEnchantedShulker(
            BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        BlockEntity blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
        if (!(blockEntity instanceof IEnchantableBlockEntity enchantableBlockEntity)) return;
        List<ItemStack> drops = cir.getReturnValue();
        NbtList enchantments = enchantableBlockEntity.getEnchantments();
        NbtCompound nbt = new NbtCompound();
        NbtCompound blockEntityTag = new NbtCompound();
        blockEntityTag.put("Enchantments", enchantments);
        nbt.put("BlockEntityTag", blockEntityTag);
        nbt.put("Enchantments", enchantments);
        for (ItemStack drop : drops) {
            NbtCompound existing = drop.getNbt();
            if (existing == null) {
                // Don't set empty Enchantments
                if (enchantments.isEmpty()) continue;
                drop.setNbt(nbt.copy());
            } else {
                // Remove `Enchantments` tags, when enchantments are empty
                if (enchantments.isEmpty()) {
                    drop.removeSubNbt("Enchantments");
                    NbtCompound existingBlockEntityTag = drop.getSubNbt("BlockEntityTag");
                    if (existingBlockEntityTag == null) continue;
                    existingBlockEntityTag.remove("Enchantments");
                    if (existingBlockEntityTag.isEmpty()) drop.removeSubNbt("BlockEntityTag");
                    continue;
                }
                drop.setNbt(nbt.copyFrom(existing));
            }
        }
    }
}