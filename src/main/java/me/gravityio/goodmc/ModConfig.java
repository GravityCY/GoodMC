package me.gravityio.goodmc;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@SuppressWarnings("CanBeFinal")
@Config(name = GoodMC.MOD_ID)
public class ModConfig implements ConfigData {
    public boolean drop_amethyst_on_explode = true;
    public boolean piston_move_budding = true;
    public boolean budding_hardness = true;
    public boolean ancient_city_cartographer = true;
    public boolean physical_lantern = true;
    public boolean animal_aging = true;
    public boolean angry_mobs = true;
    public boolean geode_compass = true;
    public boolean lantern_toggle = true;
}
