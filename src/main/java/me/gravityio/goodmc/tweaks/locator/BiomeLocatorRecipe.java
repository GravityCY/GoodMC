package me.gravityio.goodmc.tweaks.locator;

import me.gravityio.goodmc.lib.better_compass.CompassUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.LegacySmithingRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BiomeLocatorRecipe extends LegacySmithingRecipe {

    public BiomeLocatorRecipe(Identifier id) {
        super(id, Ingredient.ofItems(Items.COMPASS), Ingredient.ofItems(LocatorTweak.BIOME_TATTERED_MAP), LocatorTweak.STACK_BIOME_COMPASS);
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        if (!super.matches(inventory, world)) return false;
        return !CompassUtils.isPointing(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return this.getOutput(null).copy();
    }
}