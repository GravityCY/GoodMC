package me.gravityio.goodmc.client.tweaks.cool_lantern;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.client.tweaks.IClientTweak;
import me.gravityio.random.ArmRenderableRegistry;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("ALL")
public class CoolLanternClientTweak implements IClientTweak {

    private MinecraftClient client;
    private final LanternArmRenderable lanternArmRenderable = ArmRenderableRegistry.register(new LanternArmRenderable());
    private final KeyBinding selectLight = new KeyBinding("key.goodmc.select_light", GLFW.GLFW_KEY_LEFT_ALT, KeyBinding.INVENTORY_CATEGORY);
    private int prevSlot = -1;

    private boolean isLightItem(ItemStack stack) {
        return stack.isOf(Items.SOUL_LANTERN) || stack.isOf(Items.LANTERN) || stack.isOf(Items.TORCH);
    }
    private int toServerSlot(int a) {
        return PlayerInventory.isValidHotbarIndex(a) ? PlayerInventory.MAIN_SIZE + a : a;
    }
    private int getLightItem(PlayerInventory inventory) {
        for (int i = 0; i < inventory.main.size(); i++)
        {
            ItemStack stack = inventory.main.get(i);
            if (isLightItem(stack)) return i;
        }
        return -1;
    }
    private void swapSlot(MinecraftClient client, int a, int b) {
        a = toServerSlot(a);
        b = toServerSlot(b);
        client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, a, b, SlotActionType.SWAP, client.player);
    }
    private int equipLightItem(MinecraftClient client) {
        PlayerInventory inventory = client.player.getInventory();
        if (isLightItem(inventory.offHand.get(0))) return -1;
        int lightSlot = getLightItem(inventory);
        if (lightSlot == -1) return -1;
        swapSlot(client, lightSlot, 40);
        return lightSlot;
    }
    @Override
    public void onInit(MinecraftClient client) {
        this.client = client;
        KeyBindingHelper.registerKeyBinding(selectLight);
    }

    @Override
    public void onTick()
    {
        if (GoodMC.config.lantern_toggle) {
            while (selectLight.wasPressed()) {
                if (prevSlot == -1) {
                    prevSlot = equipLightItem(client);
                } else {
                    swapSlot(client, prevSlot, 40);
                    prevSlot = -1;
                }
            }
        } else {
            if (selectLight.isPressed()) {
                if (prevSlot == -1) {
                    prevSlot = equipLightItem(client);
                }
            } else if (prevSlot != -1) {
                swapSlot(client, prevSlot, 40);
                prevSlot = -1;
            }
        }

    }

}
