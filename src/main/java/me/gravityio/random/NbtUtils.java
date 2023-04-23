package me.gravityio.random;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class NbtUtils {
    public static BlockPos fromNbt(NbtCompound nbt) {
        int x = nbt.getInt("x");
        int y = nbt.getInt("y");
        int z = nbt.getInt("z");
        return new BlockPos(x, y, z);
    }
    public static NbtCompound toNbt(BlockPos pos) {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("x", pos.getX());
        nbt.putInt("y", pos.getY());
        nbt.putInt("z", pos.getZ());
        return nbt;
    }
}
