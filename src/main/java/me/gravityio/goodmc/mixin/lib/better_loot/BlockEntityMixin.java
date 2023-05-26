package me.gravityio.goodmc.mixin.lib.better_loot;

import me.gravityio.goodmc.lib.better_loot.BetterLootRegistry;
import me.gravityio.goodmc.mixin.interfaces.IStructureWideLootTable;
import me.gravityio.goodmc.tweaks.locator.LocatorTweak;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Makes BlockEntities get which structure they belong to in order to be able to tell if a chest with a loot table has been looted before or not within the structure
 */
@Mixin(BlockEntity.class)
public class BlockEntityMixin {
    @Shadow @Final protected BlockPos pos;

    @Inject(method = "setWorld", at = @At("TAIL"))
    private void setStructure(World world, CallbackInfo ci) {
        BlockEntity self = (BlockEntity) (Object) this;
        if (world.isClient || !(self instanceof IStructureWideLootTable blockStructureLootTable)) return;
        Identifier lootTableID = blockStructureLootTable.getLootTable();
        if (lootTableID == null) return;
        Identifier structureId = BetterLootRegistry.getStructure(lootTableID);
        if (structureId == null) return;
        ServerWorld serverWorld = (ServerWorld) world;
        Structure structure = world.getRegistryManager().get(RegistryKeys.STRUCTURE).get(structureId);
        StructureStart structureStart = serverWorld.getStructureAccessor().getStructureContaining(this.pos, structure);
        if (structureStart == null) return;
        blockStructureLootTable.setStructure(LocatorTweak.state.getStructure(structureStart, structureId));
    }
}
