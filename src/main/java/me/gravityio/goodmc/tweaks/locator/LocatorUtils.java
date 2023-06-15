package me.gravityio.goodmc.tweaks.locator;

import me.gravityio.goodlib.helper.GoodItemHelper;
import me.gravityio.goodlib.helper.GoodNbtHelper;
import me.gravityio.goodlib.helper.GoodStringHelper;
import me.gravityio.goodlib.util.LookupMap;
import me.gravityio.goodmc.mixin.interfaces.ILocatorPlayer;
import me.gravityio.goodmc.tweaks.locator.impl.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This inner class organization might class me as a psychopath, oh well ;)
 */
public class LocatorUtils {

    // COMPASS DATA
    public static class Data {
        public static final String LOCATOR_KEY = "Locator";
        public static final String MAX_USES_KEY = "MaxUses";
        public static final String USES_KEY = "Uses";
        public static void doUpdateDisplay(@NotNull ItemStack compass) {
            int uses = getLocateUses(compass);
            int maxUses = getLocateMaxUses(compass);
            doUpdateDisplay(compass, uses, maxUses);
        }

        public static void doUpdateDisplay(@NotNull ItemStack compass, @Nullable Integer uses, @Nullable Integer maxUses) {
            uses = uses == null ? getLocateUses(compass) : uses;
            maxUses = maxUses == null ? getLocateMaxUses(compass) : maxUses;

            GoodItemHelper.setLore(compass, Text.literal("Uses %d / %d".formatted(uses, maxUses))
                    .setStyle(LocatorTweak.LORE_STYLE), 1);
        }

        public static int getLocateMaxUses(@NotNull ItemStack compass) {
            NbtInt nbtInt = GoodNbtHelper.getDeep(compass.getNbt(), NbtInt.class, LOCATOR_KEY, MAX_USES_KEY);
            return nbtInt == null ? 0 : nbtInt.intValue();
        }

        public static void setLocateMaxUses(@NotNull ItemStack compass, int uses) {
            GoodNbtHelper.putDeep(compass.getOrCreateNbt(), NbtInt.of(uses),LOCATOR_KEY, MAX_USES_KEY);
        }

        /**
         * Set the NBT for uses
         * @param compass Compass
         * @param uses Uses
         */
        public static void setLocateUses(@NotNull ItemStack compass, int uses) {
            GoodNbtHelper.putDeep(compass.getOrCreateNbt(), NbtInt.of(uses), LOCATOR_KEY, USES_KEY);
        }

        /**
         * Gets the NBT for uses
         * @param compass Compass
         * @return Uses integer
         */
        public static int getLocateUses(@NotNull ItemStack compass) {
            NbtInt nbtInt = GoodNbtHelper.getDeep(compass.getNbt(), NbtInt.class, LOCATOR_KEY, USES_KEY);
            return nbtInt == null ? 0 : nbtInt.intValue();
        }

        /**
         * Gets the NBT for uses but returns null if it doesn't have NBT
         * @param compass Compass
         * @return Uses or Null
         */
        public static @Nullable Integer getLocateUsesNull(@NotNull ItemStack compass) {
            NbtInt nbtInt = GoodNbtHelper.getDeep(compass.getNbt(), NbtInt.class, LOCATOR_KEY, USES_KEY);
            return nbtInt == null ? null : nbtInt.intValue();
        }

        /**
         * Checks if it has the NBT
         * @param compass Compass
         * @return Whether it exists
         */
        public static boolean hasLocateUses(@NotNull ItemStack compass) {
            return GoodNbtHelper.containsDeep(compass.getNbt(), NbtElement.INT_TYPE, "uses");
        }

        /**
         * Adds uses to the NBT
         * @param compass Compass
         * @param add Add integer
         */
        public static void addLocateUses(@NotNull ItemStack compass, int add) {
            setLocateUses(compass, getLocateUses(compass) + add);
        }
    }

    // ACTIONS
    public static class Actions {

        public static int MAX_USES = 1;

        private static final Random random = new Random();


        /**
         * Will make the compass physically point to a POI, if the compass still has uses left
         * @param player Player
         * @param compass Compass
         * @return Action
         */
        public static TypedActionResult<ItemStack> onUse(@NotNull ServerPlayerEntity player, @NotNull ItemStack compass) {
            int uses = Data.getLocateUses(compass);
            if (uses == 0) return null;

            for (ILocatable locatable : LocatableRegistry.get(LocatableType.POI_LOCATABLE)) {
                if (!locatable.isLocatable(compass, player)) continue;
                if (locatable.locate(compass, player) && !player.isCreative()) {
                    int newUses = uses - 1;
                    Data.setLocateUses(compass, newUses);
                    Data.doUpdateDisplay(compass, newUses, null);
                }
                return TypedActionResult.consume(compass);
            }
            return null;
        }

        /**
         * Assigns a compass to a random POI or increments its uses depending on if it already has a predefined POI
         * @param player Player
         * @param compass Compass
         * @param useHand Hand compass was used in
         * @return Action
         */
        public static TypedActionResult<ItemStack> onCraft(ServerPlayerEntity player, ItemStack compass, Hand useHand) {
            Hand mapHand = Helper.getOpposite(useHand);
            ItemStack map = player.getStackInHand(mapHand);

            boolean isBiomeMap = map.isOf(LocatorTweak.BIOME_TATTERED_MAP);
            boolean isStructureMap = map.isOf(LocatorTweak.STRUCTURE_TATTERED_MAP);
            boolean isAnyMap = isBiomeMap || isStructureMap;
            if (isAnyMap) {
                boolean hasBiomeNBT = BiomeLocatable.isPointingAtBiome(compass);
                boolean hasStructureNBT = StructureLocatable.isPointingAtStructure(compass);
                boolean hasAnyNBT = hasBiomeNBT || hasStructureNBT;
                if (hasAnyNBT) {
                    if (isBiomeMap && hasBiomeNBT || isStructureMap && hasStructureNBT) {
                        int uses = Data.getLocateUses(compass);
                        int maxUses = Data.getLocateMaxUses(compass);
                        if (uses != maxUses) {
                            int newUses = uses + 1;
                            Data.setLocateUses(compass, newUses);
                            Data.doUpdateDisplay(compass, newUses, maxUses);
                            map.decrement(1);
                            return TypedActionResult.pass(compass);
                        }
                    }
                } else {
                    ItemStack crafted = Items.COMPASS.getDefaultStack();
                    int dec = player.isCreative() ? 0 : 1;
                    compass.decrement(dec);
                    map.decrement(dec);
                    if (isBiomeMap) {
                        onCraftBiome(player, crafted);
                    } else if (isStructureMap) {
                        onCraftStructure(player, crafted);
                    }
                    player.getInventory().insertStack(crafted);
                    return TypedActionResult.pass(crafted);
                }
            }

            return null;
        }

        /**
         * Points to compass to a random Structure Type, not to the position of it!
         * @param player Player
         * @param compass Compass
         */
        public static void onCraftBiome(ServerPlayerEntity player, ItemStack compass) {
            ILocatorPlayer locatorPlayer = (ILocatorPlayer) player;
            Identifier dimensionKey = player.getWorld().getRegistryKey().getValue();
            Map<Identifier, List<Identifier>> availableBiomesMap = locatorPlayer.getAvailableBiomes();
            List<Identifier> availableBiomesList = availableBiomesMap.get(dimensionKey);
            boolean areAllExcluded = availableBiomesList.isEmpty();
            if (areAllExcluded)
                availableBiomesList = BiomeLocatable.BiomeRegistry.getBiomes(dimensionKey);
            Identifier biomeKey = availableBiomesList.get(random.nextInt(availableBiomesList.size()));
            if (!areAllExcluded)
                locatorPlayer.addExcludedBiome(dimensionKey, biomeKey);
            String key = "biome.%s.%s".formatted(biomeKey.getNamespace(), biomeKey.getPath());
            MutableText biomeText = Text.translatable(key).setStyle(LocatorTweak.LORE_STYLE);
            MutableText hotbarText = biomeText.copy().setStyle(LocatorTweak.HOTBAR_STYLE);
            GoodItemHelper.setLore(compass, biomeText, 0);
            GoodItemHelper.setHotbarTooltip(compass, hotbarText);
            BiomeLocatable.setPointsTo(compass, dimensionKey, biomeKey);
            Data.setLocateUses(compass, MAX_USES);
            Data.setLocateMaxUses(compass, MAX_USES);
            Data.doUpdateDisplay(compass);
        }

        /**
         * Points to compass to a random Biome Type, not to the position of it!
         * @param player Player
         * @param compass Compass
         */
        public static void onCraftStructure(ServerPlayerEntity player, ItemStack compass) {
            ILocatorPlayer locatorPlayer = (ILocatorPlayer) player;
            Identifier dimensionKey = player.getWorld().getRegistryKey().getValue();
            Map<Identifier, List<Identifier>> availableStructuresMap = locatorPlayer.getAvailableStructures();
            List<Identifier> availableStructuresList = availableStructuresMap.get(dimensionKey);
            boolean empty = availableStructuresList.isEmpty();
            if (empty)
                availableStructuresList = StructureLocatable.StructureRegistry.getStructures(dimensionKey);
            Identifier structureKey = availableStructuresList.get(random.nextInt(availableStructuresList.size()));
            if (!empty)
                locatorPlayer.addExcludedStructure(dimensionKey, structureKey);
            String key = "structure.%s.%s".formatted(structureKey.getNamespace(), structureKey.getPath());
            MutableText loreText = Text.translatable(key).setStyle(LocatorTweak.LORE_STYLE);
            MutableText hotbarText = loreText.copy().setStyle(LocatorTweak.HOTBAR_STYLE);
            GoodItemHelper.setLore(compass, loreText, 0);
            GoodItemHelper.setHotbarTooltip(compass, hotbarText);
            StructureLocatable.setPointsTo(compass, dimensionKey, structureKey);
            Data.setLocateUses(compass, MAX_USES);
            Data.setLocateMaxUses(compass, MAX_USES);
            Data.doUpdateDisplay(compass);
        }

    }

    // HELPER
    public static class Helper {
        public static Hand getOpposite(Hand hand) {
            if (hand == Hand.MAIN_HAND)
                return Hand.OFF_HAND;
            if (hand == Hand.OFF_HAND)
                return Hand.MAIN_HAND;
            return null;
        }

        /**
         * Gets all items in a given map that are not in the exclusions map <br><br>
         * Used for getting all structures / biomes a player can roll that have not already been rolled <br><br>
         * If they've rolled minecraft:plains, that would be in the exclusions map, so we fill the dimensionalMap with all the other registered biomes that are not minecraft:plains
         * @param dimensionalMap
         * @param exclusions
         * @return
         */
        public static Map<Identifier, List<Identifier>> getAvailable(Map<Identifier, List<Identifier>> dimensionalMap, Map<Identifier, List<Identifier>> exclusions) {
            Map<Identifier, List<Identifier>> out = new HashMap<>();
            for (Map.Entry<Identifier, List<Identifier>> entry : dimensionalMap.entrySet()) {
                Identifier dimension = entry.getKey();
                LookupMap<Identifier> lookupMap = LookupMap.fromList(exclusions.computeIfAbsent(dimension, (v) -> new ArrayList<>()));
                for (Identifier identifier : entry.getValue()) {
                    out.computeIfAbsent(dimension, (v) -> new ArrayList<>());
                    if (!lookupMap.contains(identifier))
                        out.get(dimension).add(identifier);
                }
            }
            return out;
        }

        /**
         * As a fallback option when the translation key doesn't exist we use this function to turn a translation key into a somewhat displayable string
         * "minecraft:plains" -> "Plains"
         * "minecraft:the_nether" -> "The Nether"
         * "terralith:zpointer/some_people_use_directoies" -> "Some People Use Directories"
         * @param pointKey
         * @return
         */
        public static String formatPointKey(String pointKey) {
            int slash = pointKey.lastIndexOf('/');
            if (slash != -1)
                return GoodStringHelper.capitalize(pointKey.substring(slash + 1).replace("_", " "));

            int dot = pointKey.lastIndexOf('.');
            if (dot != -1)
                return GoodStringHelper.capitalize(pointKey.substring(dot + 1).replace("_", " "));

            return GoodStringHelper.capitalize(pointKey.replace("_", " "));
        }

        /**
         * Returns true if any of the strBiomes is included in dimBiomes <br>
         * Used to find if a structure belongs to a given dimension using the structures biome sources
         * @param strBiomes
         * @param dimBiomes
         * @return
         */
        public static boolean hasAnyBiomeInDimBiome(RegistryEntryList<Biome> strBiomes, Set<RegistryEntry<Biome>> dimBiomes) {
            for (RegistryEntry<Biome> biome : strBiomes) {
                Optional<RegistryKey<Biome>> opt = biome.getKey();
                if (opt.isEmpty()) continue;
                RegistryKey<Biome> strBiomeKey = opt.get();
                for (RegistryEntry<Biome> dimBiome : dimBiomes) {
                    Optional<RegistryKey<Biome>> opt1 = dimBiome.getKey();
                    if (opt1.isEmpty()) continue;
                    RegistryKey<Biome> dimBiomeKey = opt1.get();
                    if (dimBiomeKey.getValue().equals(strBiomeKey.getValue()))
                        return true;
                }
            }
            return false;
        }

        /**
         * Gets all biomes associated with a certain dimension
         * @param dimension
         * @return
         */
        public static List<Identifier> getBiomesInDimension(ServerWorld dimension) {
            List<Identifier> list = new ArrayList<>();

            for (RegistryEntry<Biome> biome : dimension.getChunkManager().getChunkGenerator().getBiomeSource().getBiomes()) {
                Optional<RegistryKey<Biome>> opt = biome.getKey();
                if (opt.isEmpty()) continue;
                list.add(opt.get().getValue());
            }
            return list;
        }

        /**
         * Gets all structures associated with a certain dimension
         * @param dimension
         * @return
         */
        public static List<Identifier> getStructuresInDimension(ServerWorld dimension) {
            List<Identifier> list = new ArrayList<>();

            Set<RegistryEntry<Biome>> dimBiomes = dimension.getChunkManager().getChunkGenerator().getBiomeSource().getBiomes();
            DynamicRegistryManager registry = dimension.getRegistryManager();
            for (Map.Entry<RegistryKey<Structure>, Structure> entry : registry.get(RegistryKeys.STRUCTURE).getEntrySet()) {
                RegistryKey<Structure> key = entry.getKey();
                Structure structure = entry.getValue();
                if (!hasAnyBiomeInDimBiome(structure.getValidBiomes(), dimBiomes)) continue;
                list.add(key.getValue());
            }

            return list;
        }
    }

}
