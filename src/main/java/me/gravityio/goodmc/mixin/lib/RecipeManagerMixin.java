package me.gravityio.goodmc.mixin.lib;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.tweaks.structure_locator.LocatorRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

/**
 * So far a mixin that just allows for me to use a custom smithing recipe that checks for whether the item has specific nbt etc
 */
@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    private static final Identifier LOCATOR_RECIPE_ID = new Identifier(GoodMC.MOD_ID, "locator_smithing");
    private static final LocatorRecipe LOCATOR_RECIPE = new LocatorRecipe(LOCATOR_RECIPE_ID);
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "INVOKE",
                     target = "java/util/Map.entrySet()Ljava/util/Set;",
                     ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onApply(Map<Identifier, JsonElement> map,
                         ResourceManager resourceManager,
                         Profiler profiler, CallbackInfo ci,
                         Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2,
                         ImmutableMap.Builder<Identifier, Recipe<?>> builder) {
        map2.computeIfAbsent(RecipeType.SMITHING, recipeType -> ImmutableMap.builder()).put(LOCATOR_RECIPE_ID, LOCATOR_RECIPE);
        builder.put(LOCATOR_RECIPE_ID, LOCATOR_RECIPE);
    }

}
