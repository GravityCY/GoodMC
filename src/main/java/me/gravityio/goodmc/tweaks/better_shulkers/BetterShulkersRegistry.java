package me.gravityio.goodmc.tweaks.better_shulkers;

import me.gravityio.goodmc.random.TriFunction;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


/**     Registry that stores Items that are used to be able to have
 *      <ul>
 *           <li>Better Shulker Enchantments on them</li>
 *           <li>Be right clicked in a shulker box inventory to open a NBT inventory </li>
 *           <li>Have custom logic for when it's clicked in that shulker box inventory (to open your custom ScreenHandler for example)</li>
 *           <li>Have custom ScreenHandlerTypes that can have the above logic (in case you have your own ScreenHandler that is also a Shulker Box</li>
 *      </ul>
 *      Currently, doesn't work for multiplayer, as I don't know how to synchronize them.
 */
public class BetterShulkersRegistry {

    private static final Set<ScreenHandlerType<?>> allowedScreens = new HashSet<>();
    private static final Map<Class<? extends ItemConvertible>, TriFunction<ItemStack, Slot, Supplier<Boolean>, NamedScreenHandlerFactory>> onSlotScreenHandlers = new HashMap<>();
    private static final List<Class<? extends ItemConvertible>> shulkers = new ArrayList<>();

    /**
     * Registers a {@link Class} that extends {@link ItemConvertible} in order to query the registry whether an item is a Shulker Item
     * @param item The Item {@link Class} that extends {@link ItemConvertible}
     */
    public static void register(Class<? extends ItemConvertible> item) {
        shulkers.add(item);
    }

    /**
     * Registers a {@link Class} to a {@link Function} in order to run when that Class Instance is clicked in a ShulkerScreenHandler
     * @param item The {@link Class} the item is of
     * @param onOpenFunction The {@link Function} that will run when the item is clicked
     */
    public static void register(Class<? extends ItemConvertible> item, TriFunction<ItemStack, Slot, Supplier<Boolean>, NamedScreenHandlerFactory> onOpenFunction) {
        onSlotScreenHandlers.put(item, onOpenFunction);
    }

    /**
     * Registers a {@link ScreenHandlerType} that will be used to check which ScreenHandlers Shulker Items can be opened in
     * @param screenHandlerType The {@link ScreenHandlerType} to register
     */
    public static void register(ScreenHandlerType<?> screenHandlerType) {
        allowedScreens.add(screenHandlerType);
    }


    /**
     * Checks if the given {@link ScreenHandlerType} is in the registry
     * @param screenHandlerType The {@link ScreenHandlerType} to check for
     * @return {@link Boolean}
     */
    public static boolean isAllowedScreen(ScreenHandlerType<?> screenHandlerType) {
        return allowedScreens.contains(screenHandlerType);
    }


    /**
     * Checks if the given {@link ItemStack} is within the registry
     * @param stack The {@link ItemStack} to check for
     * @return {@link Boolean}
     */
    public static boolean isShulker(ItemStack stack) {
        return isShulker(stack.getItem());
    }

    public static boolean isShulker(Item item) {
        for (Class<? extends ItemConvertible> shulkerClass : shulkers) {
            if (item instanceof BlockItem blockItem) {
                if (shulkerClass.isInstance(blockItem.getBlock()))
                    return true;
            } else {
                if (shulkerClass.isInstance(item))
                    return true;
            }
        }
        return false;
    }

    /**
     * Gets the associated Callback Function for the given Item
     * @param item The {@link ItemStack} to get the {@link Function} from
     * @return {@link Function}
     */
    public static TriFunction<ItemStack, Slot, Supplier<Boolean>, NamedScreenHandlerFactory> getScreenHandler(ItemConvertible item) {
        return item instanceof BlockItem blockItem ? onSlotScreenHandlers.get(blockItem.getBlock().getClass()) : onSlotScreenHandlers.get(item.getClass());
    }

    public static class Builder {
        private final List<Class<? extends ItemConvertible>> items = new ArrayList<>();
        private TriFunction<ItemStack, Slot, Supplier<Boolean>, NamedScreenHandlerFactory> onOpenFunction;
        public Builder addItem(Class<? extends ItemConvertible> item) {
            this.items.add(item);
            return this;
        }

        public Builder addItems(List<Class<? extends ItemConvertible>> items) {
            this.items.addAll(items);
            return this;
        }

        public Builder setOpenAction(TriFunction<ItemStack, Slot, Supplier<Boolean>, NamedScreenHandlerFactory> onOpenFunction) {
            this.onOpenFunction = onOpenFunction;
            return this;
        }

        public void register() {
            for (Class<? extends ItemConvertible> itemClass : items) {
                if (this.onOpenFunction != null)
                    BetterShulkersRegistry.register(itemClass, this.onOpenFunction);
                BetterShulkersRegistry.register(itemClass);
            }
        }
    }

    static {
        new Builder()
                .addItem(ShulkerBoxBlock.class)
                .setOpenAction((itemStack, slot, canOpenSupplier) -> {
                    ScreenHandlerFactory screenHandlerFactory = (syncId, playerInv, playerEntity) -> new ShulkerBoxScreenHandler(syncId, playerInv, new ContainedItemInventory(27, itemStack, slot, canOpenSupplier));
                    return new SimpleNamedScreenHandlerFactory(screenHandlerFactory, itemStack.getName());
                })
                .register();
        register(ScreenHandlerType.SHULKER_BOX);
    }
}
