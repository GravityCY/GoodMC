package me.gravityio.goodmc.tweaks;

import me.gravityio.goodmc.tweaks.better_shulkers.BetterShulkersTweak;
import me.gravityio.goodmc.tweaks.better_villagers.BetterVillagersTweak;
import me.gravityio.goodmc.tweaks.structure_locator.StructureLocatorTweak;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ServerTweaks {
    public static final List<IServerTweak> tweaks = new ArrayList<>();

    public static BetterShulkersTweak BETTER_SHULKERS = register(new BetterShulkersTweak());
    public static BetterVillagersTweak BETTER_VILLAGERS = register(new BetterVillagersTweak());
    public static StructureLocatorTweak LOCATOR = register(new StructureLocatorTweak());

    private static <T extends IServerTweak> T register(T tweak)
    {
        tweaks.add((IServerTweak) tweak);
        return tweak;
    }
}
