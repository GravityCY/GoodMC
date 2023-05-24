package me.gravityio.goodmc;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@SuppressWarnings("CanBeFinal")
@Config(name = GoodMC.MOD_ID)
public class GoodConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static GoodConfig INSTANCE;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public All all = new All();
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public Amethyst amethyst = new Amethyst();
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public RealisticLantern lantern = new RealisticLantern();
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public Locator locator = new Locator();
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public AnimalAging aging = new AnimalAging();

    public static class All {
        @ConfigEntry.Gui.Tooltip
        public boolean angry_mobs = true;

        @ConfigEntry.BoundedDiscrete(max = 100)
        public int view_bobbing_strength = 100;
    }
    public static class Locator {
        @ConfigEntry.Gui.CollapsibleObject
        public Biome biome = new Biome();
        @ConfigEntry.Gui.CollapsibleObject
        public Structure structure = new Structure();
        public static class Biome {
            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.BoundedDiscrete(min = 1, max = 4)
            public int radius = 2;
            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.BoundedDiscrete(min = 50, max = 500)
            public int update_distance = 250;
        }
        public static class Structure {
            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.BoundedDiscrete(min = 1, max = 4)
            public int radius = 1;
            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.BoundedDiscrete(min = 50, max = 500)
            public int update_distance = 200;
        }

    }
    public static class Amethyst {
        @ConfigEntry.Gui.Tooltip
        public boolean drop_amethyst_on_explode = true;
        @ConfigEntry.Gui.Tooltip
        public boolean piston_move_budding = true;
        @ConfigEntry.Gui.RequiresRestart
        @ConfigEntry.Gui.Tooltip
        public boolean budding_hardness = true;
        @ConfigEntry.Gui.Tooltip
        public boolean geode_compass = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
        public int update_distance = 5;
    }
    public static class RealisticLantern {
        @ConfigEntry.Gui.Tooltip
        public boolean physical_lantern = true;
        @ConfigEntry.Gui.Tooltip
        public boolean lantern_toggle = true;
    }
    public static class AnimalAging {
        @ConfigEntry.Gui.Tooltip
        public boolean mob_aging = true;
        @ConfigEntry.Gui.Tooltip
        public boolean grow_hitbox = false;
        @ConfigEntry.Gui.Tooltip
        public AgeMobOnly only = AgeMobOnly.ALL;
        public enum AgeMobOnly {
            ALL, VILLAGER, ANIMALS
        }

    }


}
