package me.gravityio.goodmc.mixin.mixins;

import me.gravityio.goodmc.mixin.interfaces.IEnchantableBlockEntity;
import me.gravityio.goodmc.tweaks.better_shulkers.BetterShulkersRegistry;
import me.gravityio.goodmc.tweaks.better_shulkers.BetterShulkersTweak;
import me.gravityio.goodmc.tweaks.better_shulkers.ShulkerUtils;
import me.gravityio.random.NbtUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A Mixin that makes Shulkers Boxes objectively Better (yes objectively)
 * <ul>
 *     <li>Shulkers can now be put inside of shulker boxes with {@link me.gravityio.goodmc.tweaks.better_shulkers.enchants.ShulkerRecursion Shulker Recursion} (only 1 shulker deep)</li>
 *     <li>Actually glints when it has enchantments on it (as an item or a block)</li>
 *     <li>Shulkers inside of Shulkers can now be right clicked to open their inventory</li>
 * </ul>
 * Makes Players invulnerable to Shulker Bullets when they have the {@link me.gravityio.goodmc.tweaks.better_shulkers.enchants.ShulkerAffinity Shulker Affinity} Enchant on any of their Armor
 */
@SuppressWarnings("ALL")
public class BetterShulkersMixin {

//    Server
    @Mixin(ShulkerBulletEntity.class)
    public abstract static class ShulkerAffinityImpl {
        @Inject(method="onEntityHit", at = @At(value="NEW", target="net/minecraft/entity/effect/StatusEffectInstance"), cancellable = true)
        private void onShulkerShellHit(EntityHitResult entityHitResult, CallbackInfo ci)
        {
            LivingEntity livingEntity = (LivingEntity) entityHitResult.getEntity();
            if (EnchantmentHelper.getEquipmentLevel(BetterShulkersTweak.SHULKER_AFFINITY, livingEntity) > 0)
                ci.cancel();
        }
    }
    @Mixin(ShulkerBoxBlockEntity.class)
    public abstract static class EnchantableShulkerBox extends BlockEntity implements IEnchantableBlockEntity {
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
    @Mixin(ShulkerBoxBlockEntity.class)
    public static class ShulkerBoxRecursiveness {
        @Inject(method="canInsert", at = @At("HEAD"), cancellable = true)
        private void canInsertInto(int slot, ItemStack stack, Direction dir, CallbackInfoReturnable<Boolean> cir) {
            cir.setReturnValue(ShulkerUtils.canInsert(stack, (IEnchantableBlockEntity) this));
        }

    }
    @Mixin(ShulkerBoxSlot.class)
    public static abstract class ShulkerBoxSlotRecursiveness extends Slot {
        @Shadow public abstract boolean canInsert(ItemStack stack);

        public ShulkerBoxSlotRecursiveness(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
        private void canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
            if (!(this.inventory instanceof SidedInventory)) return;
            cir.setReturnValue(((SidedInventory)this.inventory).canInsert(0, stack, null));
        }
    }
    @Mixin(AbstractBlock.class)
    public static class EnchantedShulkerDrop {
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
    @Mixin(ItemStack.class)
    public static abstract class ItemStackEnchantNBT {
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
    @Mixin(EnchantmentHelper.class)
    public static class EnchantmentHelperMixin {
        @Inject(method = "set", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
        private static void set(Map<Enchantment, Integer> enchantments, ItemStack stack, CallbackInfo ci, NbtList nbtList) {
            if (!ShulkerUtils.isShulker(stack)) return;
            NbtCompound tag = stack.getOrCreateSubNbt("BlockEntityTag");
            if (nbtList.isEmpty()) {
                tag.remove("Enchantments");
                if (tag.isEmpty()) stack.removeSubNbt("BlockEntityTag");
            } else if (!stack.isOf(Items.ENCHANTED_BOOK)) {
                tag.put("Enchantments", nbtList);
            }
        }
    }
    @Mixin(ScreenHandler.class)
    public static class OpenNestedShulkerBox {

        private static final BiFunction<ItemStack, Integer, NamedScreenHandlerFactory> defScreenHandler = BetterShulkersRegistry.getScreenHandler(Items.SHULKER_BOX);

        @Shadow @Final public DefaultedList<Slot> slots;
        @Shadow @Final private @Nullable ScreenHandlerType<?> type;

        @Inject(method="onSlotClick", at = @At("HEAD"), cancellable = true)
        public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
            if (button != GLFW.GLFW_MOUSE_BUTTON_2 || this.type == null || !BetterShulkersRegistry.isAllowedScreen(this.type)) return;
            int size = this.slots.size();
            ItemStack stack = slotIndex >= 0 && slotIndex < size - player.getInventory().main.size() ? this.slots.get(slotIndex).getStack() : ItemStack.EMPTY;
            if (stack.isEmpty() || !ShulkerUtils.isShulker(stack)) return;
            BiFunction<ItemStack, Integer, NamedScreenHandlerFactory> onOpen = BetterShulkersRegistry.getScreenHandler(stack.getItem());
            if (onOpen == null) onOpen = defScreenHandler;
            player.openHandledScreen(onOpen.apply(stack, slotIndex));
            ci.cancel();
        }
    }
    @Mixin(SimpleNamedScreenHandlerFactory.class)
    public static abstract class DontClose implements NamedScreenHandlerFactory {
        @Override
        public boolean shouldCloseCurrentScreen() {
            return false;
        }
    }
    @Mixin(LootableContainerBlockEntity.class)
    public static abstract class ItemStackPos extends LockableContainerBlockEntity {
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
    //    Client
    @Mixin(BuiltinModelItemRenderer.class)
    public static class EnchantableBlockRenderEnchants {
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
    @Mixin(ShulkerBoxBlockEntityRenderer.class)
    public static class ShulkerBoxBlockEntityRendererMixin {
        @Redirect(
                method =
                        "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
                at =
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;"))
        private VertexConsumer getVertexConsumer(
                SpriteIdentifier identifier,
                VertexConsumerProvider provider,
                Function<Identifier, RenderLayer> layerFactory,
                ShulkerBoxBlockEntity shulkerBox) {
            return ShulkerUtils.getVertexConsumer(identifier, provider, layerFactory, shulkerBox);
        }


    }
}
