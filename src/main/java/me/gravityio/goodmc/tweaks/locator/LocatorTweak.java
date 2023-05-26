package me.gravityio.goodmc.tweaks.locator;

import me.gravityio.goodmc.GoodConfig;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.lib.BetterItems;
import me.gravityio.goodmc.lib.better_compass.BiomeLocatable;
import me.gravityio.goodmc.lib.better_compass.BiomeLocatable.BiomeRegistry;
import me.gravityio.goodmc.lib.better_compass.StructureLocatable;
import me.gravityio.goodmc.lib.better_compass.StructureLocatable.StructureRegistry;
import me.gravityio.goodmc.lib.better_loot.BetterLootRegistry;
import me.gravityio.goodmc.lib.better_recipes.BetterRecipeRegistry;
import me.gravityio.goodmc.lib.events.ModEvents;
import me.gravityio.goodmc.lib.helper.ItemUtils;
import me.gravityio.goodmc.tweaks.IServerTweak;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.List;
import java.util.Random;

import static me.gravityio.goodmc.GoodMC.MOD_ID;

// TODO: Turns out compasses are not as fun as they could be, just depending on a radius still means you depend on vanillas completely random biome generation,
//  which means sometimes you just get really unlucky, and the compass should kind of be more helpful than just hey if there's a structure within 1024 blocks of you I'll point to it
//  maybe it should be more like hey there's a structure 10000 blocks away but I'm not gonna give you an accurate location of it, more like a general direction until you get closer

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

    private static final Random random = new Random();

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

        initSaveListener();
        initRegistry();
        initDefaultItems();
        ModEvents.ON_CREATE_WORLDS.register((server) -> {
            state = LootedStructuresState.getServerState(server);
            return ActionResult.SUCCESS;
        });
    }

    @Override
    public void onServerStart(MinecraftServer server) {}
    @Override
    public void onTick() {}

    // DONE: SOMEHOW DUPLICATING 2ND SLOT // I forgor to set biome tattered map to only chest loot tables...
    private ActionResult onCraft(Recipe<?> recipe, ItemStack stack, PlayerEntity player) {
        if (!(recipe instanceof SmithingRecipe) || (!(player instanceof ServerPlayerEntity serverPlayer))) return ActionResult.PASS;
        if (recipe.getId() == BIOME_RECIPE_ID) {
            Identifier dimensionKey = serverPlayer.getWorld().getRegistryKey().getValue();
            List<Identifier> biomeKeys = BiomeRegistry.getBiomes(dimensionKey);
            Identifier biomeKey = biomeKeys.get(random.nextInt(biomeKeys.size()));
            MutableText loreText = Text.translatable(String.format("biome.%s.%s",biomeKey.getNamespace(), biomeKey.getPath())).setStyle(LORE_STYLE);
            MutableText hotbarText = loreText.copy().setStyle(HOTBAR_STYLE);
            ItemUtils.setLore(stack, loreText);
            BetterItems.setHotbarTooltip(stack, hotbarText);
            BiomeLocatable.setPointsTo(stack, dimensionKey, biomeKey);
            BiomeLocatable.updateLocator(stack, serverPlayer.getWorld(), serverPlayer);
        } else if (recipe.getId() == STRUCTURE_RECIPE_ID) {
            Identifier dimensionKey = serverPlayer.getWorld().getRegistryKey().getValue();
            List<Identifier> structureKeys = StructureRegistry.getStructures(dimensionKey);
            Identifier structureKey = structureKeys.get(random.nextInt(structureKeys.size()));
            MutableText loreText = Text.translatable(String.format("structure.%s.%s", structureKey.getNamespace(), structureKey.getPath())).setStyle(LORE_STYLE);
            MutableText hotbarText = loreText.copy().setStyle(HOTBAR_STYLE);
            ItemUtils.setLore(stack, loreText);
            BetterItems.setHotbarTooltip(stack, hotbarText);
            StructureLocatable.setPointsTo(stack, dimensionKey, structureKey);
            StructureLocatable.updateLocator(stack, serverPlayer.getWorld(), serverPlayer);
        }

        return ActionResult.SUCCESS;
    }

    private void initSaveListener() {
        GoodMC.CONFIG_HOLDER.registerSaveListener((configHolder, modConfig) -> {
            GoodMC.LOGGER.debug("[LocatorTweak] Setting new Structure UPDATE_DISTANCE to {}", modConfig.locator.structure.update_distance);
            GoodMC.LOGGER.debug("[LocatorTweak] Setting new Structure RADIUS to {}", modConfig.locator.structure.radius);
            GoodMC.LOGGER.debug("[LocatorTweak] Setting new Biome UPDATE_DISTANCE to {}", modConfig.locator.biome.update_distance);
            GoodMC.LOGGER.debug("[LocatorTweak] Setting new Biome RADIUS to {}", modConfig.locator.biome.radius);
            StructureLocatable.UPDATE_DISTANCE = modConfig.locator.structure.update_distance;
            StructureLocatable.RADIUS = modConfig.locator.structure.radius;
            BiomeLocatable.UPDATE_DISTANCE = modConfig.locator.biome.update_distance;
            BiomeLocatable.RADIUS = modConfig.locator.biome.radius;
            return ActionResult.SUCCESS;
        });
    }

    private void initRegistry() {
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

        // Mod Registries
        // REGISTER ALL THE STRUCTURES FOR THE COMPASS TO USE
        StructureRegistry.registerStructure(World.OVERWORLD.getValue(),
                StructureKeys.PILLAGER_OUTPOST.getValue(),
                StructureKeys.DESERT_PYRAMID.getValue(),
                StructureKeys.JUNGLE_PYRAMID.getValue(),
                StructureKeys.ANCIENT_CITY.getValue(),
                StructureKeys.MANSION.getValue(),
                StructureKeys.MONUMENT.getValue(),
                StructureKeys.MONUMENT.getValue(),
                StructureKeys.VILLAGE_DESERT.getValue(),
                StructureKeys.VILLAGE_PLAINS.getValue(),
                StructureKeys.VILLAGE_SAVANNA.getValue(),
                StructureKeys.VILLAGE_SNOWY.getValue(),
                StructureKeys.VILLAGE_TAIGA.getValue());
        StructureRegistry.registerStructure(World.NETHER.getValue(),
                StructureKeys.BASTION_REMNANT.getValue(),
                StructureKeys.FORTRESS.getValue(),
                StructureKeys.NETHER_FOSSIL.getValue());
        StructureRegistry.registerStructure(World.END.getValue(),
                StructureKeys.END_CITY.getValue());

        BiomeRegistry.registerBiome(World.OVERWORLD.getValue(),
                BiomeKeys.PLAINS.getValue(),
                BiomeKeys.SUNFLOWER_PLAINS.getValue(),
                BiomeKeys.SNOWY_PLAINS.getValue(),
                BiomeKeys.ICE_SPIKES.getValue(),
                BiomeKeys.DESERT.getValue(),
                BiomeKeys.SWAMP.getValue(),
                BiomeKeys.MANGROVE_SWAMP.getValue(),
                BiomeKeys.FOREST.getValue(),
                BiomeKeys.FLOWER_FOREST.getValue(),
                BiomeKeys.BIRCH_FOREST.getValue(),
                BiomeKeys.DARK_FOREST.getValue(),
                BiomeKeys.TAIGA.getValue(),
                BiomeKeys.SNOWY_TAIGA.getValue(),
                BiomeKeys.SAVANNA.getValue(),
                BiomeKeys.SAVANNA_PLATEAU.getValue()
                // AND MORE
        );

        BetterLootRegistry.registerLoot(BetterLootRegistry.ALL, new Identifier(MOD_ID, "structures/tattered_map"));

        BetterRecipeRegistry.register(RecipeType.SMITHING, STRUCTURE_LOCATOR_RECIPE, BIOME_LOCATOR_RECIPE);
    }

    private void initDefaultItems() {
        // SET THE LORE OF THE DEFAULT ITEM STACKS TO BE COPIED FOR CREATIVE MENU // SMITHING TABLE
        ItemUtils.setLore(STACK_STRUCTURE_COMPASS, Text.translatable("item.goodmc.compass.structure.lore.unlocated").setStyle(LORE_STYLE));
        ItemUtils.setLore(STACK_STRUCTURE_TATTERED, Text.translatable("item.goodmc.structure_tattered_map.lore").setStyle(LORE_STYLE));
        ItemUtils.setLore(STACK_BIOME_COMPASS, Text.translatable("item.goodmc.compass.biome.lore.unlocated").setStyle(LORE_STYLE));
        ItemUtils.setLore(STACK_BIOME_TATTERED, Text.translatable("item.goodmc.biome_tattered_map.lore").setStyle(LORE_STYLE));
    }



}
