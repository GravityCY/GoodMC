package me.gravityio.goodmc.tweaks.better_shulkers;


/**
 *      Implement a Custom Shulker to work with Better Shulkers, then use the <br> {@link BetterShulkersRegistry.Builder} to add Custom Items to the registry
 *      <br><br>
 *
 *      Here's what it would look like adding the Vanilla Shulker Box to the registry <br>
 *      that also opens a ScreenHandler that works with an Nbt Based Inventory <br><br>
 *      (This is actually how I do it for the vanilla one, also this is kinda inspired by the way QuickShulker does it)
 *      <pre>{@code
 *          public class BetterShulkerIntegration implements BetterShulkerRegister {
 *              @Override
 *              public void generate() {
 *                  new BetterShulkerRegistry.Builder()
 *                  .addItem(ShulkerBoxBlock.class)
 *                  .setOpenAction((itemStack) -> {
 *                      ScreenHandlerFactory screenHandlerFactory = (syncId, playerInv, playerEntity) ->
 *                          new ShulkerBoxScreenHandler(syncId, playerInv, new ShulkerItemInventory(itemStack, 27));
 *                      return new SimpleNamedScreenHandlerFactory(screenHandlerFactory, itemStack.getName());
 *                  })
 *                 .register();
 *              }
 *          }
 *      }</pre>
 */
public interface BetterShulkersRegister {
    void generate();
}
