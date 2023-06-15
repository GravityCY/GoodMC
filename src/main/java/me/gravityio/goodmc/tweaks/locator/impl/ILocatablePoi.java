package me.gravityio.goodmc.tweaks.locator.impl;

import me.gravityio.goodlib.lib.BetterCompass;
import me.gravityio.goodmc.GoodMC;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * An interface for dealing with Classes that locate generic POI'S and assign the found locate result to a compass as BetterCompass NBT
 */
public interface ILocatablePoi extends ILocatable {

    Identifier getLocatableKey(ItemStack compass);

    SoundEvent getSoundEvent();

    BlockPos locate(ServerWorld world, Identifier locatableKey, BlockPos center);

    @Override
    default boolean locate(ItemStack compass, ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();

        Identifier dimensionKey = BetterCompass.getPointDimension(compass);
        Identifier locatableKey = getLocatableKey(compass);
        if (!world.getDimensionKey().getValue().equals(dimensionKey)) {
            GoodMC.LOGGER.debug("[ILocatablePoi] Locatable Dimension is not in current dimension");
            return false;
        }
        long start = System.nanoTime();
        BlockPos playerPos = player.getBlockPos();

        GoodMC.LOGGER.debug("[ILocatablePoi] Looking for {}", locatableKey);
        BlockPos locatedPos = locate(world, locatableKey, playerPos);
        GoodMC.LOGGER.debug("[ILocatablePoi] Elapsed Time: {}ms", (System.nanoTime() - start) / 1000000L);
        if (locatedPos == null) return false;
        GoodMC.LOGGER.debug("[ILocatablePoi] Found Locatable at {}", locatedPos);

        if (!BetterCompass.isPointingAtPosition(compass)) {
            BetterCompass.setPointsToRandom(compass, false);
            player.playSound(getSoundEvent(), SoundCategory.PLAYERS, 0.5f, 1);
        } else {
            BlockPos prevPos = BetterCompass.getPointPosition(compass);
            if (isDisallowedPosition(player, compass, prevPos, locatedPos))
                return false;
        }
        BetterCompass.setPointPosition(compass, locatedPos);
        GoodMC.LOGGER.debug("[ILocatablePoi] Setting the block position of found locatable to {}", locatedPos);
        return true;
    }

    default boolean isDisallowedPosition(ServerPlayerEntity player, ItemStack compass, BlockPos prevPos, BlockPos locatedPos) {
        if (prevPos.equals(locatedPos)) {
            GoodMC.LOGGER.debug("[ILocatablePoi] Previous position and Located position are exactly the same!");
            return true;
        }

        BlockPos playerPos = player.getBlockPos();
        double distLocated = playerPos.getSquaredDistance(locatedPos);
        double distPrev = playerPos.getSquaredDistance(BetterCompass.getPointPosition(compass));
        if (distLocated < distPrev) return false;
        GoodMC.LOGGER.debug("[ILocatablePoi] Previous position {}b is closer than the located position {}b!", Math.sqrt(distPrev), Math.sqrt(distLocated));
        return true;
    }

}
