package me.gravityio.goodmc.client.tweaks.cool_lantern;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.lib.arm_renderable.ArmRenderable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.joml.Quaternionf;

public class LanternArmRenderable implements ArmRenderable {

    private static final Item[] items = { Items.LANTERN, Items.SOUL_LANTERN };

    @Override
    public Item[] getItems() {
        return items;
    }

    @Override
    public void renderArm(MatrixStack matrices, Hand hand) {
        if (!GoodMC.CONFIG.lantern.physical_lantern) return;
        if (hand == Hand.MAIN_HAND) {
            matrices.translate(0.4, 0.6, -0.1);
            matrices.multiply(new Quaternionf(-0.1716673, 0.257501, 0, 0.9509067 ));
        } else if (hand == Hand.OFF_HAND) {
            matrices.translate(-0.4, 0.6, -0.1);
            matrices.multiply(new Quaternionf(   -0.1716673, -0.257501, 0, 0.9509067 ));
        }
    }

    @Override
    public void renderItem(MatrixStack matrices, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            matrices.translate(-0.1, 0.1, -0.5);
        } else if (hand == Hand.OFF_HAND) {
            matrices.translate(0.1, 0.1, -0.5);
        }
    }
}
