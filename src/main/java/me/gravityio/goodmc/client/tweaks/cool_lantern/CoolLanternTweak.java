package me.gravityio.goodmc.client.tweaks.cool_lantern;

import me.gravityio.goodmc.GoodConfig;
import me.gravityio.goodmc.client.tweaks.IClientTweak;
import me.gravityio.goodmc.lib.arm_renderable.ArmRenderableRegistry;
import me.gravityio.goodmc.lib.keybinds.KeyBind;
import me.gravityio.goodmc.lib.keybinds.KeybindManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import static me.gravityio.goodmc.client.GoodClientMC.CATEGORY;

/**
 * A Tweak that selects a lantern to hold in your offhand either as your holding a button or if you toggle that button<br>
 * &nbsp; This exists because I also added some nice custom rendering for holding lanterns
 */
@SuppressWarnings("ALL")
public class CoolLanternTweak implements IClientTweak {

    private MinecraftClient client;
    private final LanternArmRenderable lanternArmRenderable = ArmRenderableRegistry.register(new LanternArmRenderable());
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
        if (isLightItem(inventory.offHand.get(0))) return -2;
        int lightSlot = getLightItem(inventory);
        if (lightSlot == -1) return -1;
        swapSlot(client, lightSlot, 40);
        return lightSlot;
    }

    private boolean hasPrevSlot() {
        return this.prevSlot != -1;
    }

    private void equip() {
        prevSlot = equipLightItem(client);
    }

    private void unequip() {
        if (!hasPrevSlot()) return;
        int slot = prevSlot;
        if (prevSlot == -2)
            slot = client.player.getInventory().selectedSlot;
        swapSlot(client, slot, 40);
        prevSlot = -1;
    }

    @Override
    public void onInit(MinecraftClient client) {
        this.client = client;
        KeyBind selectLight = KeybindManager.register(KeyBind.of("key.goodmc.select_light", GLFW.GLFW_KEY_LEFT_ALT, CATEGORY));
        selectLight.setWhilePressedCallback(() -> {
            if (!GoodConfig.INSTANCE.lantern.lantern_toggle) return;
            if (!hasPrevSlot()) equip();
            else unequip();
            if (prevSlot == -2) unequip();
        });
        selectLight.setOnPressedCallback(() -> {
            if (GoodConfig.INSTANCE.lantern.lantern_toggle) return;
            if (hasPrevSlot()) return;
            equip();
        });
        selectLight.setOnReleaseCallback(() -> {
            if (GoodConfig.INSTANCE.lantern.lantern_toggle) return;
            unequip();
        });
    }

    @Override
    public void onTick()
    {
//        if (GoodConfig.INSTANCE.lantern_toggle) {
//            while (selectLight.bind.wasPressed()) {
//                if (prevSlot == -1) {
//                    prevSlot = equipLightItem(client);
//                } else {
//                    swapSlot(client, prevSlot, 40);
//                    prevSlot = -1;
//                }
//            }
//        } else {
//            if (selectLight.bind.isPressed()) {
//                if (prevSlot == -1) {
//                    prevSlot = equipLightItem(client);
//                }
//            } else if (prevSlot != -1) {
//                swapSlot(client, prevSlot, 40);
//                prevSlot = -1;
//            }
//        }
    }

}
