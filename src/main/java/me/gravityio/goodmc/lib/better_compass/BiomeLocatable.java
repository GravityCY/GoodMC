package me.gravityio.goodmc.lib.better_compass;

import com.mojang.datafixers.util.Pair;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.tweaks.locator.LocatorTweak;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeLocatable implements IMovementLocatable {
    public static final String BIOME_PATH = "biome";
    public static double UPDATE_DISTANCE = 100;
    public static int RADIUS = 1;
    private static final int BLOCKS = 512;
    private static final int HORIZONTAL_BLOCK_SKIP = 256;
    private static final int VERTICAL_BLOCK_SKIP = 64;


    public static void setPointsToBiome(ItemStack compass, Identifier biomeKey) {
        CompassUtils.getOrCreatePointsTo(compass).putString(BIOME_PATH, biomeKey.toString());
    }

    public static void setPointsTo(ItemStack compass, Identifier dimensionKey, Identifier biomeKey) {
        CompassUtils.setPointDimension(compass, dimensionKey);
        setPointsToBiome(compass, biomeKey);
        CompassUtils.setPointsToRandom(compass, true);
    }

    public static Identifier getPointBiome(ItemStack compass) {
        if (!isPointingAtBiome(compass)) return null;
        return new Identifier(CompassUtils.getPointsTo(compass).getString(BIOME_PATH));
    }

    public static boolean isPointing(ItemStack compass) {
        return CompassUtils.isPointingAtDimension(compass) && isPointingAtBiome(compass);
    }

    public static boolean isPointingAtBiome(ItemStack compass) {
        return CompassUtils.isPointing(compass) && CompassUtils.getPointsTo(compass).contains(BIOME_PATH);
    }

    public static void updateLocator(ItemStack compass, ServerWorld serverWorld, ServerPlayerEntity serverPlayer) {
        if (!compass.getItem().equals(Items.COMPASS) || !isPointing(compass)) return;
        Identifier dimensionKey = CompassUtils.getPointDimension(compass);
        Identifier biomeKey = getPointBiome(compass);
        if (!serverWorld.getDimensionKey().getValue().equals(dimensionKey)) {
            GoodMC.LOGGER.debug("[BiomeLocatable] Biome Dimension is not in current dimension");
            return;
        }
        GoodMC.LOGGER.debug("[BiomeLocatable] Looking for {}", biomeKey);
        long start = System.nanoTime();
        BlockPos playerPos = serverPlayer.getBlockPos();
        BlockPos locatedPos = locateBiome(serverWorld, biomeKey, playerPos);
        GoodMC.LOGGER.debug("[BiomeLocatable] Elapsed Time: {}ms", (System.nanoTime() - start) / 1000000L);
        if (locatedPos == null) return;
        double distLocated = Math.sqrt(playerPos.getSquaredDistance(locatedPos));
        GoodMC.LOGGER.debug("[BiomeLocatable] Found Biome at: {}", locatedPos);
        if (!CompassUtils.isPointingAtPosition(compass)) {
            CompassUtils.setPointsToRandom(compass, false);
            serverPlayer.playSound(LocatorTweak.SOUND_BIOME_LOCATED, SoundCategory.PLAYERS, 0.5f, 1);
        } else {
            double distPrev = Math.sqrt(playerPos.getSquaredDistance(CompassUtils.getPointPosition(compass)));
            if (distPrev < distLocated) {
                GoodMC.LOGGER.debug("[BiomeLocatable] Previous position {}b is closer than the located position {}b, keeping previous...", distPrev, distLocated);
//                CompassUtils.setPointStrength(compass, Math.max(Math.min(500d / distPrev, 1), 0.25d));
                return;
            }
        }

        CompassUtils.setPointPosition(compass, locatedPos);
//        CompassUtils.setPointStrength(compass, Math.max(Math.min(500d / distLocated, 1), 0.25d));
        GoodMC.LOGGER.debug("[BiomeLocatable] Setting the block position of found biome to {}", locatedPos);
    }

    public static BlockPos locateBiome(ServerWorld serverWorld, Identifier biomeKey, BlockPos center) {
        Registry<Biome> biomeRegistry = serverWorld.getRegistryManager().get(RegistryKeys.BIOME);
        Biome biome = biomeRegistry.get(biomeKey);
        RegistryEntry<Biome> biomeEntry = biomeRegistry.getEntry(biome);
        GoodMC.LOGGER.debug("[BiomeLocatable] Locating biome starting from {} with a radius of {} in dimension {}", center, RADIUS * BLOCKS, serverWorld.getRegistryKey().getValue().toString());
        Pair<BlockPos, RegistryEntry<Biome>> pair = serverWorld.locateBiome(biomeRegistryEntry -> biomeEntry == biomeRegistryEntry, center, RADIUS * BLOCKS, HORIZONTAL_BLOCK_SKIP, VERTICAL_BLOCK_SKIP);
        return pair != null ? pair.getFirst() : null;
    }

    @Override
    public boolean isLocatable(ItemStack compass, ServerPlayerEntity player) {
        return compass.isOf(Items.COMPASS) && isPointing(compass);
    }

    @Override
    public void locate(ItemStack compass, ServerPlayerEntity player) {
        updateLocator(compass, player.getWorld(), player);
    }

    @Override
    public boolean hasMoved(double distance, double velocity) {
        return distance >= UPDATE_DISTANCE * (velocity + 1);
    }

    public static class BiomeRegistry {
        private static final Map<Identifier, List<Identifier>> dimensionBiomes = new HashMap<>();
        public static void registerBiome(Identifier dimensionKey, Identifier... biomeKeys) {
            List<Identifier> biomes = dimensionBiomes.computeIfAbsent(dimensionKey, k -> new ArrayList<>());
            for (Identifier biomeKey : biomeKeys) {
                if (biomes.contains(biomeKey)) {
                    GoodMC.LOGGER.debug("[BiomeRegistry] Biome:'{}' in dimension: '{}', already exists skipping...", biomeKey, dimensionKey);
                    continue;
                }
                GoodMC.LOGGER.debug("[BiomeRegistry] Registering biome:'{}' in dimension: '{}'", biomeKey, dimensionKey);
                biomes.add(biomeKey);
            }
        }

        public static List<Identifier> getBiomes(Identifier dimensionKey) {
            return dimensionBiomes.get(dimensionKey);
        }

        public static void clear() {
            GoodMC.LOGGER.debug("[BiomeRegistry] Clearing Registry...");
            dimensionBiomes.clear();
        }
    }

}
