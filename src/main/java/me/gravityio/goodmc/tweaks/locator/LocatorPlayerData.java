package me.gravityio.goodmc.tweaks.locator;

import me.gravityio.goodmc.lib.helper.NbtUtils;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LocatorPlayerData {

    public static Function<List<Identifier>, NbtElement> ARRAYLIST_OF_IDS_TO_NBT_ELEMENT = (list) -> NbtUtils.fromList(list, identifier -> NbtString.of(identifier.toString()));
    public static Function<NbtElement, List<Identifier>> NBT_LIST_OF_IDS_TO_ARRAYLIST = (nbtList) -> NbtUtils.toList((NbtList)nbtList, new ArrayList<>(), (elem) -> new Identifier(elem.asString()));
    public static final String STRUCTURE_EXCLUSIONS_KEY = "StructureExclusions";
    public static final String BIOME_EXCLUSIONS_KEY = "BiomeExclusions";

    public Map<Identifier, List<Identifier>> excludedStructures = new HashMap<>();
    public Map<Identifier, List<Identifier>> excludedBiomes = new HashMap<>();

    public Map<Identifier, List<Identifier>> availableStructures = new HashMap<>();
    public Map<Identifier, List<Identifier>> availableBiomes = new HashMap<>();
}
