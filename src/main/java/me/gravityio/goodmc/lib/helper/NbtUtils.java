package me.gravityio.goodmc.lib.helper;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Some utilities regarding NBT that {@link net.minecraft.nbt.NbtHelper NbtHelper} doesn't have
 */
public class NbtUtils {


    private static String getUptoDot(String string) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            char c = string.charAt(i);
            if (c == '.') break;
            sb.append(c);
            if (i == string.length() - 1) {
                break;
            };
            i++;
        }
        return sb.toString();
    }


    public static boolean internalCopy(NbtCompound comp, String a, String b) {
        if (comp.get(a) == null) return false;

        comp.put(b, comp.get(a).copy());
        return true;
    }

    public static <T extends NbtElement> T getDeep(NbtCompound comp, Class<T> clazz, String... orderedPaths) {
        if (comp == null) return null;

        if (orderedPaths.length != 1)
            return getDeep(NbtUtils.get(comp, orderedPaths[0]), clazz, Arrays.stream(orderedPaths).skip(1).toArray(String[]::new));
        return clazz.cast(comp.get(orderedPaths[0]));
    }

    // tag.display.Name
//    public static <T extends NbtElement> T getDeep(NbtCompound comp, String path, Class<T> clazz) {
//        StringBuilder pathLevel = new StringBuilder();
//        path.get
//        if (!end) {
//            NbtCompound levelNbt = NbtUtils.get(comp, pathLevel.toString());
//            if (levelNbt == null) return null;
//            return getDeep(levelNbt, path.substring(i + 1), clazz);
//        } else {
//            return clazz.cast(comp.get(pathLevel.toString()));
//        }
//    }

    /**
     * Converts a {@link List} into an {@link NbtList}
     * @param list The List to convert to an NbtList
     * @param elementConverter a Function that receives your list elements and should return something that extends or is an NbtElement
     * @return {@link NbtList} from the {@link List}
     */
    public static <T> NbtList fromList(List<T> list, Function<T, NbtElement> elementConverter) {
        if (list == null || elementConverter == null) return null;

        NbtList nbtList = new NbtList();
        list.forEach(o -> {
            NbtElement converted = elementConverter.apply(o);
            if (converted != null)
                nbtList.add(converted);
        });
        return nbtList;
    }

    /**
     * Converts an {@link NbtList} into a {@link List}
     * @param nbtList The NbtList to convert to a List
     * @param list Empty List you want to convert to
     * @param elementConverter a Function that receives the NbtLists' NbtElements and converts them to your List Type
     * @return Converted {@link List}
     */
    public static <T> List<T> toList(NbtList nbtList, List<T> list, Function<NbtElement, T> elementConverter) {
        if (nbtList == null || list == null || elementConverter == null) return null;

        nbtList.forEach(t -> {
            T converted = elementConverter.apply(t);
            if (converted != null)
                list.add(converted);
        });
        return list;
    }

    /**
     * Converts a {@link Map} to an {@link NbtCompound}
     * @param map The {@link Map} to convert to an {@link NbtCompound}
     * @param keyConverter a {@link Function} that receives your maps' key and converts it into a {@link String}
     * @param valueConverter a {@link Function} that receives your maps' value and converts it into an {@link NbtElement}
     * @return The converted {@link NbtCompound}
     */
    public static <T, F> NbtCompound fromMap(Map<T, F> map, Function<T, String> keyConverter, Function<F, NbtElement> valueConverter) {
        if (map == null || keyConverter == null || valueConverter == null) return null;

        NbtCompound nbtCompound = new NbtCompound();
        map.forEach((o1, o2) -> {
            String convertedKey = keyConverter.apply(o1);
            NbtElement convertedValue = valueConverter.apply(o2);
            if (convertedKey != null && convertedValue != null)
                nbtCompound.put(convertedKey, convertedValue);
        });
        return nbtCompound;
    }


    /**
     * Converts an {@link NbtCompound} to a {@link Map}
     * @param nbtCompound The {@link NbtCompound} to convert to a {@link Map}
     * @param map Empty {@link Map} you want to convert to
     * @param keyConverter a {@link Function} that receives the {@link NbtCompound NbtCompounds'} key ({@link String}) and converts it into your {@link Map Maps'} Key Type
     * @param valueConverter a {@link Function} that receives the {@link NbtCompound NbtCompounds'} value ({@link NbtElement}) and converts it into your {@link Map Maps'} Key Type
     * @return The converted {@link Map}
     */
    public static <T, F> Map<T, F> toMap(NbtCompound nbtCompound, Map<T, F> map, Function<String, T> keyConverter, Function<NbtElement, F> valueConverter) {
        if (nbtCompound == null || map == null || keyConverter == null || valueConverter == null) return null;

        nbtCompound.getKeys().forEach(key -> {
            T convertedKey = keyConverter.apply(key);
            F convertedValue = valueConverter.apply(nbtCompound.get(key));
            if (convertedKey != null && convertedValue != null)
                map.put(convertedKey, convertedValue);
        });
        return map;
    }


    /**
     *
     * @param nbt The root NBT Compound to use to check if a sub element exists, etc.
     * @param id The sub element id to look for
     * @param type The sub element type - Get from {@link NbtElement}
     * @return The element that has been either gotten or created
     */
    public static NbtElement getOrCreate(NbtCompound nbt, String id, byte type) {
        if (type == NbtElement.LIST_TYPE) {
            NbtElement elem = nbt.get(id);
            NbtList ret = new NbtList();
            if (nbt.getType(id) == NbtElement.LIST_TYPE)
                ret = (NbtList) elem;
            nbt.put(id, ret);
            return ret;
        }
        NbtCompound ret = nbt.getCompound(id);
        if (!nbt.contains(id))
            nbt.put(id, ret);
        return ret;
    }

    /**
     *
     * @param nbt The root NBT Compound to use to check if a sub element exists, etc.
     * @param id The sub element id to look for
     * @return The element that has been either gotten or created
     */
    public static NbtCompound getOrCreate(NbtCompound nbt, String id) {
        NbtCompound ret = nbt.getCompound(id);
        if (!nbt.contains(id))
            nbt.put(id, ret);
        return ret;
    }

    /**
     *
     * @param nbt The NBT to work on
     * @param key The key inside the NBT
     * @param clazz A class of what to return
     * @return Returns the type of the class parameter
     * @param <T> The class of what to return
     */
    public static <T> T get(NbtCompound nbt, String key, Class<T> clazz) {
        return clazz.cast(nbt.get(key));
    }

    /**
     *
     * @param nbt The NBT to work on
     * @param key The key inside the NBT
     * @return Returns the type of the class parameter
     */
    public static NbtCompound get(NbtCompound nbt, String key) {
        return (NbtCompound) nbt.get(key);
    }
}
