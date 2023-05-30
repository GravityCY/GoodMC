package me.gravityio.goodmc.lib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookupMap<T>{
    private final Map<T, Boolean> lookup = new HashMap<>();

    public void add(T item) {
        lookup.put(item, true);
    }

    public boolean contains(T item) {
        return lookup.containsKey(item);
    }

    public boolean remove(T item) {
        return lookup.remove(item) != null;
    }

    public static <T> LookupMap<T> fromList(List<T> list) {
        LookupMap<T> map = new LookupMap<>();
        for (T t : list)
            map.add(t);
        return map;
    }

}
