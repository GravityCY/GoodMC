package me.gravityio.goodmc.client.tweaks;

import me.gravityio.goodmc.client.tweaks.cool_lantern.CoolLanternClientTweak;
import me.gravityio.goodmc.client.tweaks.wiki_tweak.WikiClientTweak;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ClientTweaks {
    public static final List<IClientTweak> tweaks = new ArrayList<>();
    public static final CoolLanternClientTweak LIGHT_EQUIP = ClientTweaks.register(new CoolLanternClientTweak());
    public static final WikiClientTweak WIKI_BIND = ClientTweaks.register(new WikiClientTweak());
//    public static final TodoListClientTweak TODO_LIST = ClientTweaks.register(new TodoListClientTweak());
    private static <T> T register(T tweak)
    {
        tweaks.add((IClientTweak) tweak);
        return tweak;
    }
}
