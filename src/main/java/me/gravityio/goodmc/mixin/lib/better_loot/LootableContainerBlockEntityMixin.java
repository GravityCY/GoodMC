package me.gravityio.goodmc.mixin.lib.better_loot;

import me.gravityio.goodmc.lib.better_loot.BetterLootRegistry;
import me.gravityio.goodmc.mixin.interfaces.IStructureWideLootTable;
import me.gravityio.goodmc.tweaks.locator.LocatorTweak;
import me.gravityio.goodmc.tweaks.locator.LootedStructuresState.LootableStructure;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LootableContainerBlockEntity.class)
public abstract class LootableContainerBlockEntityMixin extends LockableContainerBlockEntity implements IStructureWideLootTable {
    private LootableStructure structure;
    @Shadow @Nullable protected Identifier lootTableId;
    @Shadow public abstract void setStack(int slot, ItemStack stack);

    @Shadow protected abstract DefaultedList<ItemStack> getInvStackList();

    @Shadow protected long lootTableSeed;

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
        if (this.lootTableId == null || this.structure == null || this.structure.isLooted()) return;
        LocatorTweak.state.setLooted(this.structure);
        for (Identifier lootTableKey : BetterLootRegistry.getLoot(this.structure.structureKey)) {
            LootTable lootTable = this.world.getServer().getLootManager().getTable(lootTableKey);
            LootContext.Builder builder = new LootContext.Builder((ServerWorld) this.world).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(this.pos)).random(this.lootTableSeed);
            if (player != null) {
                builder.luck(player.getLuck()).parameter(LootContextParameters.THIS_ENTITY, player);
            }
            lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST));
        }
    }
}
