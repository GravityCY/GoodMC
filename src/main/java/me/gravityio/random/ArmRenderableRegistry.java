package me.gravityio.random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmRenderableRegistry {
    private static final List<ArmRenderable> armRenderableList = new ArrayList<>();
    public static <T extends ArmRenderable> T register(T armRenderable) {
        armRenderableList.add(armRenderable);
        return armRenderable;
    }

    public static ArmRenderable getArmRenderable(ItemStack itemStack) {
        Item item = itemStack.getItem();
        for (ArmRenderable armRenderable : armRenderableList) {
            for (Item armItem : armRenderable.getItems()) {
                if (armItem.equals(item)) return armRenderable;
            }
        }
        return null;
    }
}
