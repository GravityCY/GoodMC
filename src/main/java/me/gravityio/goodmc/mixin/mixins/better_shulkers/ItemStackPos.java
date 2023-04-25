package me.gravityio.goodmc.mixin.mixins.better_shulkers;

import me.gravityio.goodmc.tweaks.better_shulkers.ShulkerUtils;
import me.gravityio.random.NbtUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootableContainerBlockEntity.class)
public abstract class ItemStackPos extends LockableContainerBlockEntity {
    protected ItemStackPos(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "setStack(ILnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    public void setStackWithPos(int slot, ItemStack stack, CallbackInfo ci) {
        if (!ShulkerUtils.isShulker(stack)) return;
        NbtCompound pos = NbtUtils.toNbt(this.pos);
        NbtCompound parent = stack.getOrCreateNbt();
        parent.put("ParentPos", pos);
    }
}