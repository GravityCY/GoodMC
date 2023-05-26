package me.gravityio.goodmc.lib.helper;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Some utilities regarding NBT that {@link net.minecraft.nbt.NbtHelper NbtHelper} doesn't have
 */
public class NbtUtils {

    public static boolean internalCopy(NbtCompound comp, String from, String to) {
        if (comp.get(from) == null) return false;

        comp.put(to, comp.get(from).copy());
        return true;
    }

    public static <T extends NbtElement> T getOrCreateDeep(NbtCompound comp, Supplier<T> typeSupplier, Class<T> clazz, String... orderedPaths) {
        if (comp == null) return null;

        if (orderedPaths.length != 1)
            return getOrCreateDeep(NbtUtils.getOrCreate(comp, orderedPaths[0]), typeSupplier, clazz, Arrays.stream(orderedPaths).skip(1).toArray(String[]::new));
        return NbtUtils.getOrCreate(comp, orderedPaths[0], typeSupplier, clazz);
    }

    public static <T extends NbtElement> T getDeep(NbtCompound comp, Class<T> clazz, String... orderedPaths) {
        if (comp == null) return null;

        if (orderedPaths.length != 1)
            return getDeep(NbtUtils.get(comp, orderedPaths[0]), clazz, Arrays.stream(orderedPaths).skip(1).toArray(String[]::new));
        return clazz.cast(comp.get(orderedPaths[0]));
    }

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
     * This will replace any previous NBT that does not match the given type,
     * for example getOrCreate(nbt, "coolName", () -> NbtInt.of(1), NbtInt.class) but "coolName" points to a string
     * it will just end up replacing "coolName" with your given type
     * @param nbt The root NBT Compound to use to check if a sub element exists, etc.
     * @param key The sub element id to look for
     * @return The element that has been either gotten or created
     */
    public static <T extends NbtElement> T getOrCreate(NbtCompound nbt, String key, Supplier<T> typeSupplier, Class<T> clazz) {
        if (nbt == null) return null;
        NbtElement elem = nbt.get(key);
        if (!clazz.isInstance(elem))
            nbt.put(key, elem = typeSupplier.get());

        return clazz.cast(elem);
    }

    /**
     *
     * @param nbt The root NBT Compound to use to check if a sub element exists, etc.
     * @param id The sub element id to look for
     * @return The element that has been either gotten or created
     */
    public static NbtCompound getOrCreate(NbtCompound nbt, String id) {
        if (nbt == null) return null;
        NbtCompound ret = NbtUtils.get(nbt, id);
        if (ret == null)
            nbt.put(id, ret = new NbtCompound());
        return ret;
    }

    /**
     * A Nullable version of all the NbtCompound.get${type} variants <br>
     * For example NbtCompound.getList() always returns a new NbtList that is not attached to its entries <br>
     * So you'd have to do something like
     * <pre style="padding: 2px; background-color: #1e1f22">
     *{@code
     * // Either returns the entry or a newly made NbtList because it didn't exist
     * NbtList theList = nbt.getList("TheList", NbtElement.STRING_TYPE);
     * // Can we guarantee that this was even a list that was in the NBTs' entries?
     * if (theList.isEmpty())
     *   nbt.putString("TheList", theList = new NbtList());
     * }</pre> <br>
     * So this just kinda simplifies things by making you do a null check instead
     * <pre style="padding: 2px; background-color: #1e1f22">
     *{@code
     * NbtList theList = NbtUtils.get(nbt, "TheList", NbtList.class);
     * if (theList == null)
     *   nbt.put("TheList", theList = new NbtList());
     * }</pre> <br>
     * You could also just do <span style="background-color:#222">NbtList theList = (NbtList) nbt.get("TheList")</span> :/
     * @param nbt The NBT to work on
     * @param key The key inDside the NBT
     * @param clazz A class of what to return
     * @return Returns the type of the class parameter
     * @param <T> The class of what to return
     */
    public static <T> @Nullable T get(NbtCompound nbt, String key, Class<T> clazz) {
        if (nbt == null) return null;
        return clazz.cast(nbt.get(key));
    }

    /**
     * A Nullable version of
     * @param nbt The NBT to work on
     * @param key The key inside the NBT
     * @return Returns the type of the class parameter
     */
    public static @Nullable NbtCompound get(NbtCompound nbt, String key) {
        if (nbt == null) return null;
        return (NbtCompound) nbt.get(key);
    }
}
