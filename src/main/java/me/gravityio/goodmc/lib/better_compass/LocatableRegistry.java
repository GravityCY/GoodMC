package me.gravityio.goodmc.lib.better_compass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Registry containing structures that compasses can use to point towards
 */
public class LocatableRegistry {
    private static final Map<LocatableType<? extends ILocatable>, List<ILocatable>> locatables = new HashMap<>();

    static {
        register(LocatableType.MOVEMENT_LOCATABLE, new StructureLocatable());
        register(LocatableType.MOVEMENT_LOCATABLE, new BiomeLocatable());
    }

    public static void register(LocatableType<? extends ILocatable> type, ILocatable locatable) {
        if (!locatables.containsKey(type))
            locatables.put(type, new ArrayList<>());
        locatables.get(type).add(locatable);
    }

    public static <T extends ILocatable> List<T> get(LocatableType<T> type) {
        return (List<T>) locatables.get(type);
    }
}
