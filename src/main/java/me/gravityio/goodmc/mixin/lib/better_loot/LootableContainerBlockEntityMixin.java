package me.gravityio.goodmc.mixin.lib.better_loot;

import me.gravityio.goodmc.mixin.interfaces.IStructureWideLootTable;
import me.gravityio.goodmc.tweaks.structure_locator.LootedStructuresState.LootableStructure;
import me.gravityio.goodmc.tweaks.structure_locator.StructureLocatorTweak;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Debug(export = true)
@Mixin(LootableContainerBlockEntity.class)
public abstract class LootableContainerBlockEntityMixin extends LockableContainerBlockEntity implements IStructureWideLootTable {
    private LootableStructure structure;
    @Shadow @Nullable protected Identifier lootTableId;
    @Shadow public abstract void setStack(int slot, ItemStack stack);

    @Shadow protected abstract DefaultedList<ItemStack> getInvStackList();

    protected LootableContainerBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public Identifier getLootTable() {
        return this.lootTableId;
    }

    @Override
    public void setLootTable(Identifier lootTable) {
        this.lootTableId = lootTable;
    }

    @Override
    public void setStructure(LootableStructure structure) {
        this.structure = structure;
    }

    @Override
    public LootableStructure getStructure() {
        return this.structure;
    }

    @Inject(method = "checkLootInteraction", at = @At("HEAD"))
    private void addCustomLoot(PlayerEntity player, CallbackInfo ci) {
        if (this.lootTableId == null || this.structure == null) return;
        if (this.structure.isLooted()) return;
        for (int i = 0; i < this.getInvStackList().size(); i++) {
            ItemStack itemStack = this.getInvStackList().get(i);
            if (!itemStack.isEmpty()) continue;
            this.getInvStackList().set(i, StructureLocatorTweak.TATTERED_MAP_STACK.copy());
            this.markDirty();
            StructureLocatorTweak.state.setLooted(this.structure);
            return;
        }
    }
}
