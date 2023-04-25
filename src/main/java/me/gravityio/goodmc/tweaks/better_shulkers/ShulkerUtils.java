package me.gravityio.goodmc.tweaks.better_shulkers;

import me.gravityio.enchantableblocks.mixins.interfaces.IEnchantableBlock;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.random.NbtInventory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("ALL")
/**
 * A Utility class to help with shulker related things whether it'd be NBT manipulation of the Shulker Item
 *
 */
public class ShulkerUtils {

//    Shulkers can have Shulker Recursion, which allows a depth of 1 shulker box inside of it's inventory, meaning you can't have
//    a Shulker inside of a Shulker inside of the Main Shulker, you can only have a Shulker inside of the Main Shulker

    public static final TagKey<Item> SHULKER_BOXES = TagKey.of(RegistryKeys.ITEM, new Identifier(GoodMC.MOD_ID, "shulker_boxes"));

    /**
     * Returns a Vertex Consumer that has the Glint Effect based off of if the block entity has enchantments on it
     * @param identifier
     * @param provider
     * @param layerFactory
     * @param shulkerBox
     * @return
     */
    public static VertexConsumer getVertexConsumer(SpriteIdentifier identifier, VertexConsumerProvider provider, Function<Identifier, RenderLayer> layerFactory, ShulkerBoxBlockEntity shulkerBox) {
        return ShulkerUtils.shouldGlint(shulkerBox) ? identifier.getSprite().getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(provider, identifier.getRenderLayer(layerFactory), false, true)) : identifier.getVertexConsumer(provider, RenderLayer::getEntityCutoutNoCull);
    }

    /**
     * A Utility Function to let a Shulker Block Entity know whether an ItemStack can be put in it's inventory
     * @param stack
     * @param blockEntity
     * @return {@link Boolean}
     */
    public static boolean canInsert(ItemStack stack, IEnchantableBlock blockEntity) {
        int recursion = getRecursiveEnchant(blockEntity);
        int depth = getDepth(stack);
        return recursion >= depth;
    }
    public static boolean isShulker(ItemStack stack) {
        return isShulker(stack.getItem());
    }

    /**
     * Checks if an {@link Item} is either in the {@link BetterShulkersRegistry} or is in the tag <b>goodmc:shulker_boxes</b>
     * @param item
     * @return
     */
    public static boolean isShulker(Item item) {
        RegistryEntry<Item> entry = Registries.ITEM.getEntry(item);
        return BetterShulkersRegistry.isShulker(item) || entry.isIn(ShulkerUtils.SHULKER_BOXES);
    }

    /**
     * Checks if an {@link ItemStack} has Inventory NBT data <br>
     * This only works for Blocks that have Inventory NBT data, it just checks if there's a BlockEntityTag with Items tag inside of it.
     * @param stack
     * @return
     */
    public static boolean isInventory(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || nbt.isEmpty()) return false;
        NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");
        if (blockEntityTag.isEmpty()) return false;
        NbtList itemsList = blockEntityTag.getList("Items", NbtElement.COMPOUND_TYPE);
        return !itemsList.isEmpty();
    }

    /**
     * Returns an ordered inventory from an ItemStack's NBT. <br>
     * Just maps the slots to the actual ItemStack instance. <br><br>
     * I guess I could also just put the slot in the ItemStack's NBT?
     * @param stack {@link ItemStack}
     * @return {@link Map}
     */
    public static Map<Integer, ItemStack> getOrderedInventory(ItemStack stack) {
        NbtList list = NbtInventory.getNbtInventory(stack);
        Map<Integer, ItemStack> items = new HashMap<>();
        for (int i = 0; i < list.size(); i++)
        {
            NbtCompound itemCompound = list.getCompound(i);
            int slot = itemCompound.getByte("Slot");
            items.put(slot, ItemStack.fromNbt(itemCompound));
        }
        return items;
    }

    /**
     * Returns an unordered inventory from an ItemStack's NBT<br>
     * This is because the NBT Inventory doesn't store it's ItemStacks contiguously, and stores them like so [{id:"a", Slot:20}, {id:"b", Slot:25}] <br><br>
     * Index 0 of the Array would be an Item at Slot: 20 <br>
     * Index 1 of the Array would be an Item at slot: 25 <br>
     * @param stack
     * @return
     */
    public static ItemStack[] getUnorderedInventory(ItemStack stack) {
        NbtList list = NbtInventory.getNbtInventory(stack);
        ItemStack[] items = new ItemStack[list.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = ItemStack.fromNbt(list.getCompound(i));
        }
        return items;
    }

    /**
     * Gets the depth of a Shulker {@link ItemStack}. <br>
     * Depth referring to how many shulkers inside of shulkers +1 (counts the parent shulker). <br><br>
     * For example <br>
     * A Shulker that has a Shulker inside of it has a Depth of 2.
     * @param inventory
     * @return
     */
    private static int getDepth(ItemStack stack) {
        int depth = 0;
        if (isShulker(stack)) depth++;
        if (isInventory(stack)) depth += getDepth(getUnorderedInventory(stack));
        return depth;
    }

    /**
     * Gets the depth of an inventory <br>
     * Depth referring to how many shulkers inside of shulkers <br>
     * Example <br>
     * An Inventory that has a Shulker inside of it has a Depth of 1.
     * @param inventory
     * @return
     */
    private static int getDepth(ItemStack[] inventory) {
        int depth = 0;
        for (ItemStack item : inventory) {
            if (isShulker(item)) {
                int temp = getDepth(item);
                if (temp >= depth) depth = temp;
            }
        }
        return depth;
    }

    /**
     * Gets the level of {@link ShulkerRecursion} on an {@link ItemStack}.
     * @param stack
     * @return
     */
    private static int getRecursiveEnchant(ItemStack stack) {
        return EnchantmentHelper.getLevel(BetterShulkersTweak.SHULKER_RECURSION, stack);
    }

    /**
     * Gets the level of {@link ShulkerRecursion} on an IEnchantableBlock.
     * @param blockEntity
     * @return
     */
    private static int getRecursiveEnchant(IEnchantableBlock blockEntity) {
        int recursive = 0;
        Identifier id = EnchantmentHelper.getEnchantmentId(BetterShulkersTweak.SHULKER_RECURSION);
        NbtList enchants = blockEntity.getEnchantments();
        for (int i = 0; i < enchants.size(); i++)
        {
            NbtCompound enchantComp = enchants.getCompound(i);
            if (!EnchantmentHelper.getIdFromNbt(enchantComp).equals(id)) continue;
            recursive = EnchantmentHelper.getLevelFromNbt(enchantComp);
            break;
        }
        return recursive;
    }

    /**
     * Returns whether a BlockEntity should glint if it has enchantments.
     * @param blockEntity
     * @return
     * @param <T>
     */
    private static <T extends BlockEntity> boolean shouldGlint(T blockEntity) {
        if (!(blockEntity instanceof IEnchantableBlock enchantableBlockEntity)) return false;
        return !enchantableBlockEntity.getEnchantments().isEmpty();
    }


}
