package me.gravityio.goodmc.tweaks.locator.impl;

import com.mojang.datafixers.util.Pair;
import me.gravityio.goodlib.lib.BetterCompass;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.tweaks.locator.LocatorTweak;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeLocatable implements ILocatablePoi {
    public static final String BIOME_PATH = "biome";
    public static int RADIUS = 8;
    public static final int CLOSE_RANGE = 384;
    private static final int BLOCKS_PER_RADIUS = 512;
    private static final int HORIZONTAL_BLOCK_SKIP = 256;
    private static final int VERTICAL_BLOCK_SKIP = 64;

    public static void setPointsToBiome(ItemStack compass, Identifier biomeKey) {
        BetterCompass.getOrCreatePointsTo(compass).putString(BIOME_PATH, biomeKey.toString());
    }

    public static void setPointsTo(ItemStack compass, Identifier dimensionKey, Identifier biomeKey) {
        BetterCompass.setPointDimension(compass, dimensionKey);
        setPointsToBiome(compass, biomeKey);
        BetterCompass.setPointsToRandom(compass, true);
    }

    public static Identifier getPointBiome(ItemStack compass) {
        if (!isPointingAtBiome(compass)) return null;
        return new Identifier(BetterCompass.getPointsTo(compass).getString(BIOME_PATH));
    }

    public static boolean isPointing(ItemStack compass) {
        return BetterCompass.isPointingAtDimension(compass) && isPointingAtBiome(compass);
    }

    public static boolean isPointingAtBiome(ItemStack compass) {
        return BetterCompass.isPointing(compass) && BetterCompass.getPointsTo(compass).contains(BIOME_PATH);
    }

    @Override
    public Identifier getLocatableKey(ItemStack compass) {
        return getPointBiome(compass);
    }

    @Override
    public SoundEvent getSoundEvent() {
        return LocatorTweak.SOUND_BIOME_LOCATED;
    }

    @Override
    public boolean isLocatable(ItemStack compass, ServerPlayerEntity player) {
        return compass.isOf(Items.COMPASS) && isPointing(compass);
    }

    /**
     * Locates a biome
     * @param world World
     * @param biomeKey Biome
     * @param center Center
     * @return Position
     */
    @Override
    public BlockPos locate(ServerWorld world, Identifier biomeKey, BlockPos center) {
        Registry<Biome> biomeRegistry = world.getRegistryManager().get(RegistryKeys.BIOME);
        Biome biome = biomeRegistry.get(biomeKey);
        RegistryEntry<Biome> biomeEntry = biomeRegistry.getEntry(biome);
        GoodMC.LOGGER.debug("[BiomeLocatable] Locating biome starting from {} with a radius of {} in dimension {}", center, RADIUS * BLOCKS_PER_RADIUS, world.getRegistryKey().getValue().toString());
        Pair<BlockPos, RegistryEntry<Biome>> pair = world.locateBiome(biomeRegistryEntry -> biomeEntry == biomeRegistryEntry, center, RADIUS * BLOCKS_PER_RADIUS, HORIZONTAL_BLOCK_SKIP, VERTICAL_BLOCK_SKIP);
        return pair != null ? pair.getFirst() : null;
    }

    @Override
    public boolean isDisallowedPosition(ServerPlayerEntity player, ItemStack compass, BlockPos prevPos, BlockPos locatedPos) {
        boolean isSuper = ILocatablePoi.super.isDisallowedPosition(player, compass, prevPos, locatedPos);
        if (isSuper)
            return true;

        boolean isWithin = prevPos.isWithinDistance(locatedPos, CLOSE_RANGE);
        if (isWithin)
            GoodMC.LOGGER.debug("[BiomeLocatable] Located position is within {} blocks of the previous position!", CLOSE_RANGE);
        return isWithin;
    }

    //    @Override
//    public boolean isSamePosition(ServerPlayerEntity player, ItemStack compass, BlockPos prevPos, BlockPos locatedPos) {
//        ServerWorld world = player.getWorld();
//
//        world.getBiome()
//
//        GoodMC.LOGGER.debug("Biome Index of Prev: {}", b1);
//        GoodMC.LOGGER.debug("Biome Index of Located: {}", b2);
//
//        return ILocatablePoi.super.isSamePosition(player, compass, prevPos, locatedPos);
//    }

//
//    private static int getBiomeIndex(ServerWorld world, BlockPos pos) {
//        Chunk chunk = world.getChunk(pos);
//
//        BlockPos a = getA(world.getBiomeAccess(), pos);
//
//
//        int i = BiomeCoords.fromBlock(chunk.getBottomY());
//        int j = i + BiomeCoords.fromBlock(chunk.getHeight()) - 1;
//        int k = MathHelper.clamp(pos.getY(), i, j);
//        int l = chunk.getSectionIndex(k);
//        return (l << 2 | pos.getZ()) << 2 | pos.getX();
//    }
//
//    private static BlockPos getA(BiomeAccess access, BlockPos pos) {
//        int p;
//        int i = pos.getX() - 2;
//        int j = pos.getY() - 2;
//        int k = pos.getZ() - 2;
//        int l = i >> 2;
//        int m = j >> 2;
//        int n = k >> 2;
//        double d = (double)(i & 3) / 4.0;
//        double e = (double)(j & 3) / 4.0;
//        double f = (double)(k & 3) / 4.0;
//        int o = 0;
//        double g = Double.POSITIVE_INFINITY;
//        for (p = 0; p < 8; ++p) {
//            boolean bl3;
//            boolean bl2;
//            boolean bl = (p & 4) == 0;
//            int q = bl ? l : l + 1;
//            double v = BiomeAccess.method_38106(access.seed, q,
//                    (bl2 = (p & 2) == 0) ? m : m + 1,
//                    (bl3 = (p & 1) == 0) ? n : n + 1,
//                    bl ? d : d - 1.0,
//                    bl2 ? e : e - 1.0,
//                    bl3 ? f : f - 1.0);
//            if (!(g > v)) continue;
//            o = p;
//            g = v;
//        }
//        p = (o & 4) == 0 ? l : l + 1;
//        int w = (o & 2) == 0 ? m : m + 1;
//        int x = (o & 1) == 0 ? n : n + 1;
//        return new BlockPos(p, w, x);
//    }

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

        public static Map<Identifier, List<Identifier>> getDimensionBiomes() {
            return dimensionBiomes;
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
