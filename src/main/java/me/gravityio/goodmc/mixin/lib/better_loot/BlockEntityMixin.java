package me.gravityio.goodmc.mixin.lib.better_loot;

import it.unimi.dsi.fastutil.longs.LongSet;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.mixin.interfaces.better_loot.ILootedStructure;
import me.gravityio.goodmc.mixin.interfaces.better_loot.IStructureAccessor;
import me.gravityio.goodmc.mixin.interfaces.better_loot.IStructureLootable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;


/**
 * Makes BlockEntities get which structure they belong to in order to be able to tell if a chest with a loot table has been looted before or not within the structure
 */
@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

  @Shadow @Nullable protected World world;
  @Shadow public abstract void setWorld(World world);
  @Inject(method = "setWorld", at = @At("TAIL"))
  private void setStructure(World world, CallbackInfo ci) {
    BlockEntity self = (BlockEntity) (Object) this;
    if (world.isClient || !(self instanceof IStructureLootable blockStructureLootTable) || !blockStructureLootTable.hasLootable()) return;

    ServerWorld serverWorld = (ServerWorld) world;
    StructureStart start = getStart(serverWorld, self.getPos());
    if (start == null) {
      GoodMC.LOGGER.debug("Couldn't find a structure for Pos {}", self.getPos());
      return;
    }
    ILootedStructure structure = (ILootedStructure) (Object) start;
    if (structure.getId() == null)
      structure.setId(serverWorld.getRegistryManager().get(RegistryKeys.STRUCTURE).getId(start.getStructure()));
    GoodMC.LOGGER.debug("Setting Structure of Pos {} to {}, {}", self.getPos(), structure.getId(), start);
    blockStructureLootTable.setStructure((ILootedStructure) (Object) start);
  }

  private static StructureStart getStart(ServerWorld world, BlockPos pos) {
    StructureAccessor accessor = world.getStructureAccessor();
    for (Map.Entry<Structure, LongSet> entry : accessor.getStructureReferences(pos).entrySet()) {
      for (Long l : entry.getValue()) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(new ChunkPos(l), world.getBottomSectionCoord());
        StructureStart start = world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS).getStructureStart(entry.getKey());
        if (start == null || !start.hasChildren() || !start.getBoundingBox().contains(pos)) continue;
        return start;
      }
    }
    return null;
  }
}
