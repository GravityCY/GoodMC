package me.gravityio.goodmc.lib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.ActionResult;

public class ModEvents {

    public static final Event<OnCraftEvent> ON_CRAFT = EventFactory.createArrayBacked(OnCraftEvent.class,
        listeners -> (recipe, stack, player) -> {
        for (OnCraftEvent onCraftEvent : listeners) {
            ActionResult result = onCraftEvent.onCraft(recipe, stack, player);
            if (result != ActionResult.PASS)
                return result;
        }
        return ActionResult.PASS;
    });

    public static final Event<OnBeforeCraft> ON_BEFORE_CRAFT = EventFactory.createArrayBacked(OnBeforeCraft.class,
        listeners -> (recipe, stack, player) -> {
        for (OnBeforeCraft onCraftEvent : listeners) {
            ActionResult result = onCraftEvent.onBeforeCraft(recipe, stack, player);
            if (result != ActionResult.PASS)
                return result;
        }
        return ActionResult.PASS;
    });

    public interface OnBeforeCraft {
        ActionResult onBeforeCraft(Recipe<?> recipe, ItemStack copy, PlayerEntity player);
    }

    public interface OnCraftEvent {
        ActionResult onCraft(Recipe<?> recipe, ItemStack stack, PlayerEntity player);

    }
}
