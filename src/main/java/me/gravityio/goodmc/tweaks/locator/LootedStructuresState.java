package me.gravityio.goodmc.tweaks.locator;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.lib.helper.NbtUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
// TODO: MOVE TO LIBRARIES
public class LootedStructuresState extends PersistentState {
    private static final String LOOTED_STRUCTURES = "LootedStructures";
    public List<LootableStructure> lootedStructures = new ArrayList<>();

    public static LootedStructuresState getServerState(MinecraftServer server) {
        PersistentStateManager stateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        return stateManager.getOrCreate(LootedStructuresState::createFromNbt, LootedStructuresState::new, GoodMC.MOD_ID + "_looted_structures");
    }

    public static LootedStructuresState createFromNbt(NbtCompound nbt) {
        LootedStructuresState state = new LootedStructuresState();
        GoodMC.LOGGER.debug("[LootedStructuresState] Loading State from NBT: {}", nbt.getList(LOOTED_STRUCTURES, NbtElement.COMPOUND_TYPE));
        toList(nbt, state.lootedStructures);
        return state;
    }

    public static void toList(NbtCompound nbt, List<LootableStructure> list) {
        NbtUtils.toList(nbt.getList(LOOTED_STRUCTURES, NbtElement.COMPOUND_TYPE), list, element -> LootableStructure.fromNbt((NbtCompound) element));
    }
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("LootedStructures", NbtUtils.fromList(lootedStructures, LootableStructure::toNbt));
        GoodMC.LOGGER.debug("[LootedStructuresState] Saving State to NBT: {}", nbt.getList(LOOTED_STRUCTURES, NbtElement.COMPOUND_TYPE));
        return nbt;
    }

    public LootableStructure getStructure(StructureStart start, Identifier structureKey) {
        for (LootableStructure lootedStructure : this.lootedStructures) {
            if (lootedStructure.structureKey.equals(structureKey) && lootedStructure.pos.equals(start.getPos()))
                return lootedStructure;
        }
        GoodMC.LOGGER.debug("[LootedStructuresState] Couldn't find loaded structure for ID: {} at {} creating new one", structureKey, start.getPos());
        LootableStructure structure = new LootableStructure(structureKey, start.getPos());
        this.lootedStructures.add(structure);
        return structure;
    }

    public void setLooted(LootableStructure structure) {
        GoodMC.LOGGER.debug("[LootedStructuresState] Setting Structure {} to Looted", structure);
        structure.setLooted(true);
        this.markDirty();
    }


    public static class LootableStructure {
        public final Identifier structureKey;
        private final ChunkPos pos;
        private boolean isLooted = false;
        public static LootableStructure fromNbt(NbtCompound nbt) {
            String id = nbt.getString("id");
            int x = nbt.getInt("X");
            int z = nbt.getInt("Z");
            GoodMC.LOGGER.debug("[LootableStructure] Loading LootableStructure from NBT, ID: {}, POS: [X:{}, Z:{}]", id, x, z);
            LootableStructure structure = new LootableStructure(new Identifier(id), new ChunkPos(x, z));
            structure.isLooted = true;
            return structure;
        }
        public static NbtCompound toNbt(LootableStructure structure) {
            if (!structure.isLooted) return null;
            NbtCompound nbt = new NbtCompound();
            nbt.putString("id", structure.structureKey.toString());
            nbt.putInt("X", structure.pos.x);
            nbt.putInt("Z", structure.pos.z);
            return nbt;
        }
        public static LootableStructure of(StructureStart start, Identifier structureKey) {
            return new LootableStructure(structureKey, start.getPos());
        }

        public LootableStructure(Identifier structureKey, ChunkPos pos) {
            this.structureKey = structureKey;
            this.pos = pos;
        }

        @Override
        public String toString() {
            return "ID: " + structureKey + ", POS: [X:"+pos.x+", Z:"+pos.z+"]";
        }

        public boolean isLooted() {
            return isLooted;
        }

        public void setLooted(boolean looted) {
            this.isLooted = looted;
        }
    }
}