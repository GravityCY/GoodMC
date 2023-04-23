package me.gravityio.goodmc.tweaks.better_villagers;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.tweaks.IServerTweak;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.map.MapIcon;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

/**
 * A Tweak that makes the Cartographer sell a map to the Ancient City
 */
public class BetterVillagersTweak implements IServerTweak {

    @Override
    public void onInit() {
        if (!GoodMC.config.ancient_city_cartographer) return;
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 3, factories -> factories.add(new TradeOffers.SellMapFactory(
                64,
                TagKey.of(RegistryKeys.STRUCTURE, new Identifier("minecraft:ancient_city")),
                "filled_map.ancient_city",
                MapIcon.Type.RED_X,
                1,
                3)));
    }

    @Override
    public void onServerStart(MinecraftServer server) {

    }

    @Override
    public void onTick() {

    }
}
