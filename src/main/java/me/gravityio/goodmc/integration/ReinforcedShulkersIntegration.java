package me.gravityio.goodmc.integration;

import atonkish.reinfcore.screen.ModScreenHandlerType;
import atonkish.reinfcore.screen.ReinforcedStorageScreenHandler;
import atonkish.reinfshulker.api.ReinforcedShulkerBoxesModInitializer;
import atonkish.reinfshulker.block.ReinforcedShulkerBoxBlock;
import atonkish.reinfshulker.util.ReinforcingMaterialSettings;
import me.gravityio.goodmc.tweaks.better_shulkers.BetterShulkersRegistry;
import me.gravityio.goodmc.tweaks.better_shulkers.ContainedItemInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;

/**
 * When Reinforced Shulkers is Initialized registers it's blocks, items and ScreenHandlers to the BetterShulkersRegistry
 */
public class ReinforcedShulkersIntegration implements ReinforcedShulkerBoxesModInitializer {
    @Override
    public void onInitializeReinforcedShulkerBoxes() {
        new BetterShulkersRegistry.Builder()
                .addItem(ReinforcedShulkerBoxBlock.class)
                .setOpenAction((itemStack, slot, booleanSupplier) -> {
                    ReinforcedShulkerBoxBlock block = (ReinforcedShulkerBoxBlock) ((BlockItem) itemStack.getItem()).getBlock();
                    ScreenHandlerFactory factory = (syncId, playerInventory, player) -> {
                        ContainedItemInventory itemInventory = ContainedItemInventory.getFromStack(itemStack);
                        if (itemInventory == null) itemInventory = ContainedItemInventory.make(block.getMaterial().getSize(), itemStack, slot, booleanSupplier);
                        return ReinforcedStorageScreenHandler.createShulkerBoxScreen(block.getMaterial(), syncId, playerInventory, itemInventory);
                    };
                    return new SimpleNamedScreenHandlerFactory(factory, itemStack.getName());
                })
                .register();
        for (ReinforcingMaterialSettings setting : ReinforcingMaterialSettings.values()) {
            ScreenHandlerType<ReinforcedStorageScreenHandler> screenHandlerType = ModScreenHandlerType.REINFORCED_SHULKER_BOX_MAP.get(setting.getMaterial());
            BetterShulkersRegistry.register(screenHandlerType);
        }
    }
}
