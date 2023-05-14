package me.gravityio.goodmc.tweaks.better_shulkers;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.tweaks.IServerTweak;
import me.gravityio.goodmc.tweaks.better_shulkers.enchants.ShulkerAffinity;
import me.gravityio.goodmc.tweaks.better_shulkers.enchants.ShulkerRecursion;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.item.Items;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

/**
 * A Tweak to register the Enchants: {@link ShulkerRecursion Shulker Recursion} and {@link ShulkerAffinity Shulker Affinity} <br>
 * <ul>
 *     <li>{@link ShulkerRecursion Shulker Recursion} spawns in Bastion Treasure Chests (90%)</li>
 *     <li>{@link ShulkerAffinity Shulker Affinity} spawns in End City Treasure Chests (10%)</li>
 * </ul>
 */
@SuppressWarnings("ALL")
public class BetterShulkersTweak implements IServerTweak {
    public static final ShulkerRecursion SHULKER_RECURSION = new ShulkerRecursion();
    public static final ShulkerAffinity SHULKER_AFFINITY = new ShulkerAffinity();
    @Override
    public void onInit() {
        Registry.register(Registries.ENCHANTMENT, new Identifier(GoodMC.MOD_ID, "shulker_recursion"), SHULKER_RECURSION);
        Registry.register(Registries.ENCHANTMENT, new Identifier(GoodMC.MOD_ID, "shulker_affinity"), SHULKER_AFFINITY);
        LootTableEvents.MODIFY.register(this::onLootTable);
    }

    @Override
    public void onServerStart(MinecraftServer server) {

    }

    @Override
    public void onTick() {
    }

    private void onLootTable(ResourceManager resourceManager, LootManager manager, Identifier id, LootTable.Builder tableBuilder, LootTableSource source) {
        if (source.isBuiltin()) {
            if (LootTables.BASTION_TREASURE_CHEST.equals(id)) {
                GoodMC.LOGGER.debug("<BetterShulkersTweak> Registering Loot Table BASTION_TREASURE_CHEST");
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(BinomialLootNumberProvider.create(1, 0.9f))
                        .with(ItemEntry.builder(Items.BOOK).apply(EnchantRandomlyLootFunction.create().add(SHULKER_RECURSION)));
                tableBuilder.pool(poolBuilder);
            } else if (LootTables.END_CITY_TREASURE_CHEST.equals(id)) {
                GoodMC.LOGGER.debug("<BetterShulkersTweak> Registering Loot Table END_CITY_TREASURE_CHEST");
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(BinomialLootNumberProvider.create(1, 0.1f))
                        .with(ItemEntry.builder(Items.BOOK).apply(EnchantRandomlyLootFunction.create().add(SHULKER_AFFINITY)));
                tableBuilder.pool(poolBuilder);
            }
        }
    }
}
