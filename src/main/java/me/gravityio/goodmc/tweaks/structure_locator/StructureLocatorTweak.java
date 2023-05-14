package me.gravityio.goodmc.tweaks.structure_locator;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.lib.better_compass.CompassLocatableRegistry;
import me.gravityio.goodmc.lib.better_compass.StructureLocatorUtils;
import me.gravityio.goodmc.lib.events.ModEvents;
import me.gravityio.goodmc.lib.utils.ItemUtils;
import me.gravityio.goodmc.tweaks.IServerTweak;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.List;
import java.util.Random;


/**
 * A 'tweak' that adds a Tattered Map Item that will always spawn 1 in <b>ANY</b> structure <br>
 * &nbsp; You then use this item to merge it with a compass in a smithing table in order to get a random roll
 * (based in the dimension your currently in) of a structure that the compass will point to
 */
public class StructureLocatorTweak implements IServerTweak {
    public static final Style LORE_STYLE = Style.EMPTY.withItalic(false).withFormatting(Formatting.GRAY);
    public static final Style HOTBAR_STYLE = Style.EMPTY.withFormatting(Formatting.LIGHT_PURPLE);
    public static final Identifier TATTERED_MAP_ID = new Identifier(GoodMC.MOD_ID, "tattered_map");
    public static final Item TATTERED_MAP = new Item(new Item.Settings());

    public static final ItemStack COMPASS_STACK = new ItemStack(Items.COMPASS);
    public static final ItemStack TATTERED_MAP_STACK = new ItemStack(TATTERED_MAP);

    public static final SoundEvent LOCATED_SOUND = SoundEvent.of(new Identifier(GoodMC.MOD_ID, "item.compass.located_structure"));

    public static LootedStructuresState state;

    private static final Random random = new Random();

    static {
        // SET THE LORE OF THE DEFAULT ITEM STACKS TO BE COPIED FOR CREATIVE MENU // SMITHING TABLE
        ItemUtils.setLore(COMPASS_STACK, new Text[]{Text.translatable("item.goodmc.compass.lore.unlocated").setStyle(LORE_STYLE)});
        ItemUtils.setLore(TATTERED_MAP_STACK, new Text[]{Text.translatable("item.goodmc.tattered_map.lore").setStyle(LORE_STYLE)});

        // REGISTER ALL THE STRUCTURES FOR THE COMPASS TO USE
        CompassLocatableRegistry.register(World.OVERWORLD.getValue(), new Identifier[] {
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
                StructureKeys.VILLAGE_TAIGA.getValue(),
        });
        CompassLocatableRegistry.register(World.NETHER.getValue(), new Identifier[] {
                StructureKeys.BASTION_REMNANT.getValue(),
                StructureKeys.FORTRESS.getValue(),
                StructureKeys.NETHER_FOSSIL.getValue()
        });
        CompassLocatableRegistry.register(World.END.getValue(), new Identifier[]{
                StructureKeys.END_CITY.getValue()
        });
    }

    /**
     * ON_BEFORE_CRAFT is needed because shift clicking on a stack in a smithing table behaves differently than when you just pick up the crafting result like how
     * the ON_CRAFT implementation does it where it just simply mixes into onTakeOutput but yet again this will only work for this instance
     * if anything is higher than 1 of an item count there's cases where this can break
     */
    @Override
    public void onInit() {
        Registry.register(Registries.SOUND_EVENT, LOCATED_SOUND.getId(), LOCATED_SOUND);
        Registry.register(Registries.ITEM, TATTERED_MAP_ID, TATTERED_MAP);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(TATTERED_MAP_STACK.copy()));
        ModEvents.ON_BEFORE_CRAFT.register(this::onCraft);
        ModEvents.ON_CRAFT.register(this::onCraft);
    }


    private ActionResult onCraft(ModEvents.OnCraftEvent.CraftType craftType, ItemStack stack, PlayerEntity player) {
        if (!craftType.equals(ModEvents.OnCraftEvent.CraftType.SMITHING) || (!(player instanceof ServerPlayerEntity serverPlayer))) return ActionResult.PASS;
        Identifier dimensionKey = serverPlayer.getWorld().getRegistryKey().getValue();
        List<Identifier> structureKeys = CompassLocatableRegistry.get(dimensionKey);
        Identifier structureKey = structureKeys.get(random.nextInt(structureKeys.size()));
        MutableText loreText = Text.translatable("structure." + structureKey.getNamespace() + "." + structureKey.getPath()).setStyle(LORE_STYLE);
        MutableText hotbarText = loreText.copy().setStyle(HOTBAR_STYLE);
        ItemUtils.setLore(stack, new Text[] { loreText });
        ItemUtils.setHotbarTooltip(stack, hotbarText);
        StructureLocatorUtils.setPointsTo(stack, new CompassLocatableRegistry.PointData(dimensionKey, structureKey));
        StructureLocatorUtils.updateLocator(stack, serverPlayer.getWorld(), serverPlayer);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onServerStart(MinecraftServer server) {
        state = LootedStructuresState.getServerState(server);
    }
    @Override
    public void onTick() {

    }



}
