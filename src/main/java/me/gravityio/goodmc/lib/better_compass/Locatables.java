package me.gravityio.goodmc.lib.better_compass;

import java.util.ArrayList;
import java.util.List;

public class Locatables {

    public static List<ILocatable> locatableList = new ArrayList<>();
    public static List<IMovementLocatable> movementLocatables = new ArrayList<>();

    public static StructureLocatable STRUCTURE_LOCATABLE = register(new StructureLocatable());
    public static BiomeLocatable BIOME_LOCATABLE = register(new BiomeLocatable());

    public static <T extends ILocatable> T register(T locatable) {
        locatableList.add(locatable);
        return locatable;
    }

    public static <T extends IMovementLocatable> T register(T locatable) {
        movementLocatables.add(locatable);
        locatableList.add(locatable);
        return locatable;
    }

}
