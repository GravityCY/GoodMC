package me.gravityio.goodmc.tweaks.structure_locator;

import me.gravityio.goodmc.lib.better_compass.CompassUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class LocatorRecipe extends SmithingRecipe {

    public LocatorRecipe(Identifier RECIPE_ID) {
        super(RECIPE_ID, Ingredient.ofItems(Items.COMPASS), Ingredient.ofItems(StructureLocatorTweak.TATTERED_MAP), StructureLocatorTweak.COMPASS_STACK);
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        if (!super.matches(inventory, world)) return false;
        return !CompassUtils.isPointing(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.getOutput().copy();
    }
}
