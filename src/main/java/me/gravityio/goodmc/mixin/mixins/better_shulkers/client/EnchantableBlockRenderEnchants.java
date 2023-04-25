package me.gravityio.goodmc.mixin.mixins.better_shulkers.client;

import me.gravityio.goodmc.mixin.interfaces.IEnchantableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BuiltinModelItemRenderer.class)
public class EnchantableBlockRenderEnchants {
    @Inject(
            method = "render",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;renderEntity(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)Z"),
                     locals = LocalCapture.CAPTURE_FAILSOFT)
    public void render(
            ItemStack stack,
            ModelTransformation.Mode mode,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            CallbackInfo ci,
            Item item,
            Block block,
            BlockEntity blockEntity) {

        if (!(blockEntity instanceof IEnchantableBlockEntity enchantableBlock)) return;
        enchantableBlock.setEnchantments(stack.getEnchantments());
    }
}