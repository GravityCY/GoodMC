package me.gravityio.goodmc.client.tweaks;

import me.gravityio.goodmc.client.tweaks.better_amethyst.BetterAmethystTweak;
import me.gravityio.goodmc.client.tweaks.cool_lantern.CoolLanternClientTweak;
import me.gravityio.goodmc.client.tweaks.todo_list.TodoListClientTweak;
import me.gravityio.goodmc.client.tweaks.view_bobbing.ViewBobbingTweak;
import me.gravityio.goodmc.client.tweaks.wiki_tweak.WikiClientTweak;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ClientTweaks {
    public static final List<IClientTweak> tweaks = new ArrayList<>();
    public static final CoolLanternClientTweak LIGHT_EQUIP = register(new CoolLanternClientTweak());
    public static final WikiClientTweak WIKI_BIND = register(new WikiClientTweak());
    public static final TodoListClientTweak TODO_LIST = register(new TodoListClientTweak());
    public static final ViewBobbingTweak VIEW_BOBBING = register(new ViewBobbingTweak());
    public static final BetterAmethystTweak BETTER_AMETHYST = register(new BetterAmethystTweak());
    private static <T extends IClientTweak> T register(T tweak)
    {
        tweaks.add((IClientTweak) tweak);
        return tweak;
    }
}
