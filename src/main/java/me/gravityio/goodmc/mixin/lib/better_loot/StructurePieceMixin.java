package me.gravityio.goodmc.mixin.lib.better_loot;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.mixin.interfaces.better_loot.ILootedStructure;
import me.gravityio.goodmc.mixin.interfaces.better_loot.IStructureAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructurePiece.class)
public class StructurePieceMixin {
//  @Inject(method = "addChest(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Identifier;Lnet/minecraft/block/BlockState;)Z", at = @At("RETURN"))
//  private void onAddChest(ServerWorldAccess world, BlockBox boundingBox, Random random, BlockPos pos, Identifier lootTableId, BlockState block, CallbackInfoReturnable<Boolean> cir) {
//    if (!(world.getBlockEntity(pos) instanceof ChestBlockEntity blockEntity) || !(world instanceof ServerWorld serverWorld)) return;
//    StructurePiece self = (StructurePiece) (Object) this;
//    RegistryKey<Structure> structureKey;
//    if (self instanceof DesertTempleGenerator) {
//      structureKey = StructureKeys.DESERT_PYRAMID;
//    } else if (self instanceof JungleTempleGenerator) {
//      structureKey = StructureKeys.JUNGLE_PYRAMID;
//    } else {
//      return;
//    }
//    setStructureStart(serverWorld, pos, blockEntity, structureKey);
//  }
//
//  private void setStructureStart(ServerWorld serverWorld, BlockPos pos, BlockEntity blockEntity, RegistryKey<Structure> key) {
//    Structure current = serverWorld.getRegistryManager().get(RegistryKeys.STRUCTURE).get(key);
//    StructureStart start = serverWorld.getChunk(pos.getX(), pos.getZ(), ChunkStatus.STRUCTURE_STARTS).getStructureStart(current);
//    if (start == null) {
//      GoodMC.LOGGER.debug("[StructurePieceMixin] Structure start doesn't exist at {} for {}", pos, key);
//      return;
//    }
//    GoodMC.LOGGER.debug("[StructurePieceMixin] Set structure start for {} to {}", pos, key);
//    ((IStructureAccessor)blockEntity).setStructure((ILootedStructure) (Object) start);
//  }
}
