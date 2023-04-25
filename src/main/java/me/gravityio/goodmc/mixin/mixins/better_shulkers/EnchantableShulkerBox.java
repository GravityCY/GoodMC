package me.gravityio.goodmc.mixin.mixins.better_shulkers;

import me.gravityio.goodmc.mixin.interfaces.IEnchantableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class EnchantableShulkerBox extends BlockEntity implements IEnchantableBlockEntity {
    private NbtList enchantments = new NbtList();

    public EnchantableShulkerBox(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public NbtList getEnchantments() {
        return this.enchantments;
    }
    @Override
    public void setEnchantments(NbtList enchantments) {
        this.enchantments = enchantments;
    }
    @Inject(method = "readNbt", at = @At("TAIL"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("Enchantments", NbtElement.LIST_TYPE)) {
            setEnchantments(nbt.getList("Enchantments", NbtElement.COMPOUND_TYPE));
        }
    }
    @Inject(method = "writeNbt", at = @At("TAIL"))
    public void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.put("Enchantments", this.enchantments);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.toClientNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity) (Object)this);
    }
}