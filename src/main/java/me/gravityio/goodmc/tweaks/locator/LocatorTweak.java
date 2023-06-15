package me.gravityio.goodmc.tweaks.locator;

import me.gravityio.goodlib.events.GoodEvents;
import me.gravityio.goodlib.helper.GoodItemHelper;
import me.gravityio.goodlib.lib.BetterLootRegistry;
import me.gravityio.goodlib.lib.MissingTranslation;
import me.gravityio.goodmc.GoodConfig;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.tweaks.IServerTweak;
import me.gravityio.goodmc.tweaks.locator.impl.BiomeLocatable;
import me.gravityio.goodmc.tweaks.locator.impl.LocatableRegistry;
import me.gravityio.goodmc.tweaks.locator.impl.LocatableType;
import me.gravityio.goodmc.tweaks.locator.impl.StructureLocatable;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

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
 * You then use this item to merge it with a compass in a smithing table in order to get a random roll
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
        // Config
        StructureLocatable.RADIUS = GoodConfig.INSTANCE.locator.structure.radius;
        BiomeLocatable.RADIUS = GoodConfig.INSTANCE.locator.biome.radius;

        // Events
        GoodMC.CONFIG_HOLDER.registerSaveListener(this::onSave);
        GoodEvents.ON_ITEM_USE.register(this::onItemUse);
        GoodEvents.ON_MISSING_TRANSLATION.register(this::onMissingTranslation);

        // Content
        initDefaultItems();
        initVanillaRegistries();
        BetterLootRegistry.registerLoot(BetterLootRegistry.ALL, new Identifier(MOD_ID, "structures/tattered_map"));
        LocatableRegistry.register(LocatableType.POI_LOCATABLE, new StructureLocatable());
        LocatableRegistry.register(LocatableType.POI_LOCATABLE, new BiomeLocatable());
    }

    @Override
    public void onServerStart(MinecraftServer server) {
        this.server = server;
        initLocatableRegistry();
    }

    @Override
    public void onTick() {}

    private ActionResult onSave(ConfigHolder<GoodConfig> holder, GoodConfig config) {
        initLocatableRegistry();
        GoodMC.LOGGER.debug("[LocatorTweak] Setting new Structure RADIUS to {}", config.locator.structure.radius);
        GoodMC.LOGGER.debug("[LocatorTweak] Setting new Biome RADIUS to {}", config.locator.biome.radius);
        StructureLocatable.RADIUS = config.locator.structure.radius;
        BiomeLocatable.RADIUS = config.locator.biome.radius;
        return ActionResult.SUCCESS;
    }

    private String onMissingTranslation(String key) {
        String newString = MissingTranslation.get(key);
        if (newString == null) {
            if (!key.matches("^(structure|biome)\\..*")) return null;
            newString = MissingTranslation.add(key, LocatorUtils.Helper.formatPointKey(key));
        } else {
            newString = MissingTranslation.get(key);
        }
        return newString;
    }

    private TypedActionResult<ItemStack> onItemUse(World world, PlayerEntity player, Hand useHand) {
        if (world.isClient) return null;
        ItemStack compass = player.getStackInHand(useHand);
        if (!compass.isOf(Items.COMPASS)) return null;
        TypedActionResult<ItemStack> crafted = LocatorUtils.Actions.onCraft((ServerPlayerEntity) player, compass, useHand);
        return crafted != null ? crafted : LocatorUtils.Actions.onUse((ServerPlayerEntity) player, compass);
    }

    /**
     * Everytime it saves, checks whether structure exclusions and biome exclusions from config have been changed and updates the registry to match accordingly <br>
     * Keeps track of the previous exclusion instances and whether they match equally with the new exclusions when saved
     */
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
    }

    private void initStructureRegistry() {
        StructureLocatable.StructureRegistry.clear();
        GoodMC.LOGGER.debug("[LocatorTweak] Initializing Structure Registry");
        this.server.getWorlds().forEach(serverWorld -> {
            Identifier dimension = serverWorld.getRegistryKey().getValue();
            List<Identifier> structures = LocatorUtils.Helper.getStructuresInDimension(serverWorld);
            for (Identifier structure : structures) {
                if (GoodConfig.INSTANCE.locator.structure.exclusions.contains(structure.toString())) continue;
                StructureLocatable.StructureRegistry.registerStructure(dimension, structure);
            }
        });
        prevStrConf = GoodConfig.INSTANCE.locator.structure.exclusions;
    }

    private void initBiomeRegistry() {
        BiomeLocatable.BiomeRegistry.clear();
        GoodMC.LOGGER.debug("[LocatorTweak] Initializing Biome Registry");
        this.server.getWorlds().forEach(serverWorld -> {
            Identifier dimension = serverWorld.getRegistryKey().getValue();
            List<Identifier> biomes = LocatorUtils.Helper.getBiomesInDimension(serverWorld);
            for (Identifier biome: biomes) {
                if (GoodConfig.INSTANCE.locator.biome.exclusions.contains(biome.toString())) continue;
                BiomeLocatable.BiomeRegistry.registerBiome(dimension, biome);
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
        GoodItemHelper.setLore(STACK_STRUCTURE_TATTERED, Text.translatable("item.goodmc.structure_tattered_map.lore").setStyle(LORE_STYLE));
        GoodItemHelper.setLore(STACK_BIOME_TATTERED, Text.translatable("item.goodmc.biome_tattered_map.lore").setStyle(LORE_STYLE));

        GoodItemHelper.setLore(STACK_STRUCTURE_COMPASS, Text.translatable("item.goodmc.compass.structure.lore.unlocated").setStyle(LORE_STYLE));
        GoodItemHelper.setLore(STACK_BIOME_COMPASS, Text.translatable("item.goodmc.compass.biome.lore.unlocated").setStyle(LORE_STYLE));
    }

}
