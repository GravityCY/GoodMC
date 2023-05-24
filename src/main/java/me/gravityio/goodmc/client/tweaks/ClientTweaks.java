package me.gravityio.goodmc.client.tweaks;

import me.gravityio.goodmc.client.tweaks.better_amethyst.BetterAmethystTweak;
import me.gravityio.goodmc.client.tweaks.cool_lantern.CoolLanternTweak;
import me.gravityio.goodmc.client.tweaks.view_bobbing.ViewBobbingTweak;
import me.gravityio.goodmc.client.tweaks.wiki_tweak.WikiTweak;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ClientTweaks {
    public static final List<IClientTweak> tweaks = new ArrayList<>();
    public static final CoolLanternTweak LIGHT_EQUIP = register(new CoolLanternTweak());
    public static final WikiTweak WIKI_BIND = register(new WikiTweak());
//    public static final TodoListClientTweak TODO_LIST = register(new TodoListClientTweak());
    public static final ViewBobbingTweak VIEW_BOBBING = register(new ViewBobbingTweak());
    public static final BetterAmethystTweak BETTER_AMETHYST = register(new BetterAmethystTweak());

    private static <T extends IClientTweak> T register(T tweak)
    {
        tweaks.add((IClientTweak) tweak);
        return tweak;
    }
}
