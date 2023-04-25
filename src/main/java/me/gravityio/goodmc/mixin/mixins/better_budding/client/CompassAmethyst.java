package me.gravityio.goodmc.mixin.mixins.better_budding.client;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassAnglePredicateProvider.class)
public abstract class CompassAmethyst {
    @Shadow
    protected abstract float getAimlessAngle(int seed, long time);

    private static final Block AmethystBlock = Blocks.AMETHYST_BLOCK;
    private static final Block BuddingBlock = Blocks.BUDDING_AMETHYST;

    private boolean isAmethyst(Block block) {
        return block == AmethystBlock || block == BuddingBlock;
    }

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/util/math/GlobalPos.getPos ()Lnet/minecraft/util/math/BlockPos;"), method = "getAngle", cancellable = true)
    private void randomAngleInAmethystColumn(ItemStack stack, ClientWorld world, int seed, Entity entity, CallbackInfoReturnable<Float> cir) {
        if (!GoodMC.config.geode_compass) return;
        BlockPos player = entity.getBlockPos();
        for (int i = world.getBottomY() + 6; i < 30; i++) {
            BlockPos pos = new BlockPos(player.getX(), i, player.getZ());
            if (isAmethyst(world.getBlockState(pos).getBlock())) {
                cir.setReturnValue(this.getAimlessAngle(seed, world.getTime()));
                return;
            }
        }
    }
}