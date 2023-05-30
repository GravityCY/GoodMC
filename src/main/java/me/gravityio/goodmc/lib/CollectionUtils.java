package me.gravityio.goodmc.lib;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {

    public static <T> List<T> withoutExclusions(List<T> all, List<T> exclusions) {
        List<T> out = new ArrayList<>();
        LookupMap<T> exclusiveLookup = LookupMap.fromList(exclusions);
        for (T identifier : all) {
            if (!exclusiveLookup.contains(identifier))
                out.add(identifier);
        }
        return out;
    }

}
