package me.gravityio.goodmc.lib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.MinecraftServer;
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
            for (OnBeforeCraft listener : listeners) {
                ActionResult result = listener.onBeforeCraft(recipe, stack, player);
                if (result != ActionResult.PASS)
                    return result;
            }
            return ActionResult.PASS;
        });

    public static final Event<OnCreateWorlds> ON_CREATE_WORLDS = EventFactory.createArrayBacked(OnCreateWorlds.class,
        listeners -> (server) -> {
            for (OnCreateWorlds listener : listeners) {
                ActionResult result = listener.onCreateWorlds(server);
                if (result != ActionResult.PASS)
                    return result;
            }
            return ActionResult.PASS;
        });


    public static final Event<MissingTranslation> ON_MISSING_TRANSLATION = EventFactory.createArrayBacked(MissingTranslation.class,
        listeners -> (key, newText) -> {
            for (MissingTranslation listener : listeners) {
                ActionResult result = listener.onMissingTranslation(key, newText);
                if (result != ActionResult.PASS)
                    return result;
            }
            return ActionResult.PASS;
        });

    public interface MissingTranslation {
        ActionResult onMissingTranslation(String key, StringBuilder newText);
    }

    public interface OnCreateWorlds {
        ActionResult onCreateWorlds(MinecraftServer server);
    }

    public interface OnBeforeCraft {
        ActionResult onBeforeCraft(Recipe<?> recipe, ItemStack copy, PlayerEntity player);
    }

    public interface OnCraftEvent {
        ActionResult onCraft(Recipe<?> recipe, ItemStack stack, PlayerEntity player);

    }
}
