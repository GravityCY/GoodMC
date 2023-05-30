package me.gravityio.goodmc.tweaks.locator;

import me.gravityio.goodmc.GoodConfig;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.lib.*;
import me.gravityio.goodmc.lib.better_compass.BiomeLocatable;
import me.gravityio.goodmc.lib.better_compass.BiomeLocatable.BiomeRegistry;
import me.gravityio.goodmc.lib.better_compass.StructureLocatable;
import me.gravityio.goodmc.lib.better_compass.StructureLocatable.StructureRegistry;
import me.gravityio.goodmc.lib.better_loot.BetterLootRegistry;
import me.gravityio.goodmc.lib.better_recipes.BetterRecipeRegistry;
import me.gravityio.goodmc.lib.events.ModEvents;
import me.gravityio.goodmc.lib.helper.ItemUtils;
import me.gravityio.goodmc.mixin.interfaces.ILocatorPlayer;
import me.gravityio.goodmc.tweaks.IServerTweak;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

import java.util.*;

import static me.gravityio.goodmc.GoodMC.MOD_ID;

// TODO: Make compasses a bit more helpful

//  THINK: Turns out compasses are not as fun as they could be, just searching every so and so blocks still means you depend on vanillas completely random biome generation,
//      which means sometimes you just get really unlucky, and the compass should kind of be more helpful than just hey if there's a structure within 1024 blocks of you I'll point to it
//      maybe it should be more like hey there's a structure 10000 blocks away but I'm not gonna give you an accurate location of it, more like a general direction until you get closer
//      but how do you even optimize that, since right now it runs the locate function every certain amount of blocks, because what if the player walks away from the currently pointed
//      structure and there could be a closer structure so you have to update the position, you either
//      1. Do some math to only try to locate for new structures if the player is moving away from the currently pointed structure?
//      But what if, yeah the player is walking away from the current one and if it can't find another structure it'll keep locating the same structure again and again,
//      effectively that'll be a lag machine till it finds a new structure, but maybe don't search if trying to search within previous searched positions
//      ;
//      2. You force the player to go to that structure and then when they're nearby you can start doing checks again? Which will return that structure nearby him
//      but maybe you can add a list of already explored positions?
//      3. Maybe this all just needs a custom locating function instead of depending on the vanilla one, but that sounds difficult, also threads sound like an interesting option

/*
 * If moving towards pointed structure don't search
 * If moving away from pointed structure and not in radius of previous search position search
 */

/**
 * A 'tweak' that adds a Tattered Map Item that will always spawn 1 in <b>ANY</b> structure <br>
 * &nbsp; You then use this item to merge it with a compass in a smithing table in order to get a random roll
 * (based in the dimension your currently in) of a structure that the compass will point to
 */
public class LocatorTweak implements IServerTweak {

    public static final Style LORE_STYLE = Style.EMPTY.withItalic(false).withFormatting(Formatting.GRAY);
    public static final Style HOTBAR_STYLE = Style.EMPTY.withFormatting(Formatting.LIGHT_PURPLE);
    // MAPS
    public static final Identifier STRUCTURE_TATTERED_MAP_ID = new Identifier(MOD_ID, "structure_tattered_map");
    public static final Item STRUCTURE_TATTERED_MAP = new Item(new Item.Settings());
    public static final Identifier BIOME_TATTERED_MAP_ID = new Identifier(MOD_ID, "biome_tattered_map");
    public static final Item BIOME_TATTERED_MAP = new Item(new Item.Settings());

    // ITEM STACKS
    public static final ItemStack STACK_STRUCTURE_COMPASS = new ItemStack(Items.COMPASS);
    public static final ItemStack STACK_BIOME_COMPASS = new ItemStack(Items.COMPASS);
    public static final ItemStack STACK_STRUCTURE_TATTERED = new ItemStack(STRUCTURE_TATTERED_MAP);
    public static final ItemStack STACK_BIOME_TATTERED = new ItemStack(BIOME_TATTERED_MAP);

    public static final SoundEvent SOUND_STRUCTURE_LOCATED = SoundEvent.of(new Identifier(MOD_ID, "item.compass.structure.located"));
    public static final SoundEvent SOUND_BIOME_LOCATED = SoundEvent.of(new Identifier(MOD_ID, "item.compass.biome.located"));

    private static final Identifier STRUCTURE_RECIPE_ID = new Identifier(GoodMC.MOD_ID, "structure_locator_smithing");
    private static final Identifier BIOME_RECIPE_ID = new Identifier(GoodMC.MOD_ID, "biome_locator_smithing");
    private static final StructureLocatorRecipe STRUCTURE_LOCATOR_RECIPE = new StructureLocatorRecipe(STRUCTURE_RECIPE_ID);
    private static final BiomeLocatorRecipe BIOME_LOCATOR_RECIPE = new BiomeLocatorRecipe(BIOME_RECIPE_ID);
    public static LootedStructuresState state;
    private final Random random = new Random();
    private MinecraftServer server;

    /**
     * Previous Config Structure Exclusions
     * Used to track whether anything in the config regarding exclusions has changed in order to re-initialize the registry
     */
    private List<String> prevStrConf;
    /**
     * Previous Config Biome Exclusions<br>
     * Used to track whether anything in the config regarding exclusions has changed in order to re-initialize the registry
     */
    private List<String> prevBiomeConf;

    /**
     * ON_BEFORE_CRAFT is needed because ON_CRAFT doesn't execute when shift clicking on a stack in a smithing table
     */
    @Override
    public void onInit() {
        StructureLocatable.UPDATE_DISTANCE = GoodConfig.INSTANCE.locator.structure.update_distance;
        StructureLocatable.RADIUS = GoodConfig.INSTANCE.locator.structure.radius;
        BiomeLocatable.UPDATE_DISTANCE = GoodConfig.INSTANCE.locator.biome.update_distance;
        BiomeLocatable.RADIUS = GoodConfig.INSTANCE.locator.biome.radius;

        ModEvents.ON_BEFORE_CRAFT.register(this::onCraft);
        ModEvents.ON_CRAFT.register(this::onCraft);

        GoodMC.CONFIG_HOLDER.registerSaveListener(this::onSave);
        initVanillaRegistries();
        initDefaultItems();
        ModEvents.ON_CREATE_WORLDS.register((server) -> {
            state = LootedStructuresState.getServerState(server);
            return ActionResult.SUCCESS;
        });
        ModEvents.ON_MISSING_TRANSLATION.register((key, newText) -> {
            // structure.minecraft.the_name
            // biome.minecraft.the_name
            String newString = MissingTranslation.get(key);
            if (newString == null) {
                if (!key.matches("^(structure|biome)\\..*")) return ActionResult.PASS;
                newString = MissingTranslation.add(key, formatPointKey(key));
            } else {
                newString = MissingTranslation.get(key);
            }
            newText.append(newString);
            return ActionResult.SUCCESS;
        });
        BetterLootRegistry.registerLoot(BetterLootRegistry.ALL, new Identifier(MOD_ID, "structures/tattered_map"));
    }

    @Override
    public void onServerStart(MinecraftServer server) {
        this.server = server;
        initLocatableRegistry();
    }

    @Override
    public void onTick() {}

    // DONE: SOMEHOW DUPLICATING 2ND SLOT // I forgor to set biome tattered map to only chest loot tables...
    private ActionResult onCraft(Recipe<?> recipe, ItemStack stack, PlayerEntity player) {
        if (!(recipe instanceof SmithingRecipe) || (!(player instanceof ServerPlayerEntity serverPlayer))) return ActionResult.PASS;
        ILocatorPlayer locatorPlayer = (ILocatorPlayer) player;
        if (recipe.getId() == BIOME_RECIPE_ID) {
            Identifier dimensionKey = serverPlayer.getWorld().getRegistryKey().getValue();
            Map<Identifier, List<Identifier>> availableBiomesMap = locatorPlayer.getAvailableBiomes();
            List<Identifier> availableBiomesList = availableBiomesMap.get(dimensionKey);
            boolean empty = availableBiomesList.isEmpty();
            if (empty)
                availableBiomesList = BiomeRegistry.getBiomes(dimensionKey);
            Identifier biomeKey = availableBiomesList.get(random.nextInt(availableBiomesList.size()));
            if (!empty)
                locatorPlayer.addExcludedBiome(dimensionKey, biomeKey);
            String key = String.format("biome.%s.%s",biomeKey.getNamespace(), biomeKey.getPath());
            MutableText loreText = Text.translatable(key).setStyle(LORE_STYLE);
            MutableText hotbarText = loreText.copy().setStyle(HOTBAR_STYLE);
            ItemUtils.setLore(stack, loreText);
            BetterItems.setHotbarTooltip(stack, hotbarText);
            BiomeLocatable.setPointsTo(stack, dimensionKey, biomeKey);
            BiomeLocatable.updateLocator(stack, serverPlayer.getWorld(), serverPlayer);
        } else if (recipe.getId() == STRUCTURE_RECIPE_ID) {
            Identifier dimensionKey = serverPlayer.getWorld().getRegistryKey().getValue();
            Map<Identifier, List<Identifier>> availableStructuresMap = locatorPlayer.getAvailableStructures();
            List<Identifier> availableStructuresList = availableStructuresMap.get(dimensionKey);
            boolean empty = availableStructuresList.isEmpty();
            if (empty)
                availableStructuresList = StructureRegistry.getStructures(dimensionKey);
            Identifier structureKey = availableStructuresList.get(random.nextInt(availableStructuresList.size()));
            if (!empty)
                locatorPlayer.addExcludedStructure(dimensionKey, structureKey);
            MutableText loreText = Text.translatable(String.format("structure.%s.%s", structureKey.getNamespace(), structureKey.getPath())).setStyle(LORE_STYLE);
            MutableText hotbarText = loreText.copy().setStyle(HOTBAR_STYLE);
            ItemUtils.setLore(stack, loreText);
            BetterItems.setHotbarTooltip(stack, hotbarText);
            StructureLocatable.setPointsTo(stack, dimensionKey, structureKey);
            StructureLocatable.updateLocator(stack, serverPlayer.getWorld(), serverPlayer);
        }

        return ActionResult.SUCCESS;
    }

    /**
     * Everytime it saves, checks whether structure exclusions and biome exclusions from config have been changed and updates the registry to match accordingly <br>
     * Keeps track of the previous exclusion instances and whether they match equally with the new exclusions when saved
     */
    private ActionResult onSave(ConfigHolder<GoodConfig> holder, GoodConfig config) {
        initLocatableRegistry();

        GoodMC.LOGGER.debug("[LocatorTweak] Setting new Structure UPDATE_DISTANCE to {}", config.locator.structure.update_distance);
        GoodMC.LOGGER.debug("[LocatorTweak] Setting new Structure RADIUS to {}", config.locator.structure.radius);
        GoodMC.LOGGER.debug("[LocatorTweak] Setting new Biome UPDATE_DISTANCE to {}", config.locator.biome.update_distance);
        GoodMC.LOGGER.debug("[LocatorTweak] Setting new Biome RADIUS to {}", config.locator.biome.radius);
        StructureLocatable.UPDATE_DISTANCE = config.locator.structure.update_distance;
        StructureLocatable.RADIUS = config.locator.structure.radius;
        BiomeLocatable.UPDATE_DISTANCE = config.locator.biome.update_distance;
        BiomeLocatable.RADIUS = config.locator.biome.radius;
        return ActionResult.SUCCESS;
    }

    private boolean hasStructureConfigChanged() {
        return !GoodConfig.INSTANCE.locator.structure.exclusions.equals(prevStrConf);
    }
    private boolean hasBiomeConfigChanged() {
        return !GoodConfig.INSTANCE.locator.biome.exclusions.equals(prevBiomeConf);
    }

    private void initVanillaRegistries() {
        // Vanilla Registries
        Registry.register(Registries.SOUND_EVENT, SOUND_STRUCTURE_LOCATED.getId(), SOUND_STRUCTURE_LOCATED);
        Registry.register(Registries.ITEM, STRUCTURE_TATTERED_MAP_ID, STRUCTURE_TATTERED_MAP);
        Registry.register(Registries.ITEM, BIOME_TATTERED_MAP_ID, BIOME_TATTERED_MAP);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
                    entries.add(STACK_STRUCTURE_TATTERED.copy());
                    entries.add(STACK_BIOME_TATTERED.copy());
                }
        );
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && id.getPath().startsWith("chests/")) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(BinomialLootNumberProvider.create(1, 0.1f))
                        .with(ItemEntry.builder(BIOME_TATTERED_MAP));
                tableBuilder.pool(poolBuilder);
            }
        });
        BetterRecipeRegistry.register(RecipeType.SMITHING, STRUCTURE_LOCATOR_RECIPE, BIOME_LOCATOR_RECIPE);
    }
    private void initStructureRegistry() {
        StructureRegistry.clear();
        GoodMC.LOGGER.debug("[LocatorTweak] Initializing Structure Registry");
        this.server.getWorlds().forEach(serverWorld -> {
            Identifier dimension = serverWorld.getRegistryKey().getValue();
            List<Identifier> structures = getStructuresInDimension(serverWorld);
            for (Identifier structure : structures) {
                if (GoodConfig.INSTANCE.locator.structure.exclusions.contains(structure.toString())) continue;
                StructureRegistry.registerStructure(dimension, structure);
            }
        });
        prevStrConf = GoodConfig.INSTANCE.locator.structure.exclusions;
    }
    private void initBiomeRegistry() {
        BiomeRegistry.clear();
        GoodMC.LOGGER.debug("[LocatorTweak] Initializing Biome Registry");
        this.server.getWorlds().forEach(serverWorld -> {
            Identifier dimension = serverWorld.getRegistryKey().getValue();
            List<Identifier> biomes = getBiomesInDimension(serverWorld);
            for (Identifier biome: biomes) {
                if (GoodConfig.INSTANCE.locator.biome.exclusions.contains(biome.toString())) continue;
                BiomeRegistry.registerBiome(dimension, biome);
            }
        });
        prevBiomeConf = GoodConfig.INSTANCE.locator.biome.exclusions;
    }

    /**
     * Gets called at server start
     */
    private void initLocatableRegistry() {
        if (server == null || !server.isRunning()) return;

        GoodMC.LOGGER.debug("[LocatorTweak] Initializing Locatable Registry");

        if (prevStrConf == null || hasStructureConfigChanged())
            initStructureRegistry();
        if (prevBiomeConf == null || hasBiomeConfigChanged())
            initBiomeRegistry();
    }

    private void initDefaultItems() {
        // SET THE LORE OF THE DEFAULT ITEM STACKS TO BE COPIED FOR CREATIVE MENU // SMITHING TABLE
        ItemUtils.setLore(STACK_STRUCTURE_COMPASS, Text.translatable("item.goodmc.compass.structure.lore.unlocated").setStyle(LORE_STYLE));
        ItemUtils.setLore(STACK_STRUCTURE_TATTERED, Text.translatable("item.goodmc.structure_tattered_map.lore").setStyle(LORE_STYLE));
        ItemUtils.setLore(STACK_BIOME_COMPASS, Text.translatable("item.goodmc.compass.biome.lore.unlocated").setStyle(LORE_STYLE));
        ItemUtils.setLore(STACK_BIOME_TATTERED, Text.translatable("item.goodmc.biome_tattered_map.lore").setStyle(LORE_STYLE));
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
                if (!lookupMap.contains(identifier))
                    out.computeIfAbsent(dimension, (v) -> new ArrayList<>()).add(identifier);
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
    private static String formatPointKey(String pointKey) {
        int slash = pointKey.lastIndexOf('/');
        if (slash != -1)
            return StringUtils.capitalize(pointKey.substring(slash + 1).replace("_", " "));

        int dot = pointKey.lastIndexOf('.');
        if (dot != -1)
            return StringUtils.capitalize(pointKey.substring(dot + 1).replace("_", " "));

        return StringUtils.capitalize(pointKey.replace("_", " "));
    }

    /**
     * Returns true if any of the strBiomes is included in dimBiomes <br>
     * Used to find if a structure belongs to a given dimension using the structures biome sources
     * @param strBiomes
     * @param dimBiomes
     * @return
     */
    private static boolean hasAnyBiomeInDimBiome(RegistryEntryList<Biome> strBiomes, Set<RegistryEntry<Biome>> dimBiomes) {
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
    private static List<Identifier> getBiomesInDimension(ServerWorld dimension) {
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
    private static List<Identifier> getStructuresInDimension(ServerWorld dimension) {
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
