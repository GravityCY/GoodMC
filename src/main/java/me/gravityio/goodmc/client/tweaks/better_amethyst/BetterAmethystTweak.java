package me.gravityio.goodmc.client.tweaks.better_amethyst;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.client.tweaks.IClientTweak;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BetterAmethystTweak implements IClientTweak {
    public static double UPDATE_DISTANCE = GoodMC.CONFIG.amethyst.update_distance;
    private static final Block AmethystBlock = Blocks.AMETHYST_BLOCK;
    private static final Block BuddingBlock = Blocks.BUDDING_AMETHYST;

    private static boolean isAmethyst(BlockState block) {
        return block.isOf(AmethystBlock) || block.isOf(BuddingBlock);
    }

    public static List<ItemStack> getCompasses(DefaultedList<ItemStack> items) {
        List<ItemStack> compasses = new ArrayList<>();
        for (ItemStack item : items) {
            if (item.isOf(Items.COMPASS)) compasses.add(item);
        }
        return compasses;
    }

    public static boolean nearAmethyst(World world, BlockPos searchPos) {
        int x = searchPos.getX();
        int z = searchPos.getZ();
        for (int y = world.getBottomY() + 6; y < 30; y++) {
            BlockPos yPos = new BlockPos(x, y, z);
            if (isAmethyst(world.getBlockState(yPos)))
                return true;
        }
        return false;
    }

    @Override
    public void onInit(MinecraftClient client) {
        GoodMC.CONFIG_HOLDER.registerSaveListener((configHolder, modConfig) -> {
            GoodMC.LOGGER.debug("<BetterAmethystTweak> Setting new UPDATE_DISTANCE to {}", modConfig.amethyst.update_distance);
            UPDATE_DISTANCE = modConfig.amethyst.update_distance;
            return ActionResult.SUCCESS;
        });
    }

    @Override
    public void onTick() {

    }
}
