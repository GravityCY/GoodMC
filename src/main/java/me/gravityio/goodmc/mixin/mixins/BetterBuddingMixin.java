package me.gravityio.goodmc.mixin.mixins;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BuddingAmethystBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes Budding Amethyst slightly better by
 * <ul>
 *     <li>Allowing it to be pushable by pistons</li>
 *     <li>Dropping when a creeper explodes it</li>
 *     <li>Has a higher block hardness making harder to accidently break</li>
 *     <li>Compasses freak out when above or below any type of amethyst blocks</li>
 * </ul>
 */
@SuppressWarnings("ALL")
public class BetterBuddingMixin {

    @Mixin(BuddingAmethystBlock.class)
    public static class BuddingAmethystBlockDrop extends Block {

        public BuddingAmethystBlockDrop(Settings settings) {
            super(settings);
        }

        @Redirect(method = "getPistonBehavior", at = @At(value = "FIELD",
                target = "net/minecraft/block/piston/PistonBehavior.DESTROY : Lnet/minecraft/block/piston/PistonBehavior;",
                opcode = Opcodes.GETSTATIC))
        public PistonBehavior makeMovable() {
            return GoodMC.config.piston_move_budding ? PistonBehavior.PUSH_ONLY : PistonBehavior.DESTROY;
        }

        @Override
        public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
            if (!GoodMC.config.drop_amethyst_on_explode) return;
            if (!(explosion.getEntity() instanceof CreeperEntity)) return;
            world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), Items.BUDDING_AMETHYST.getDefaultStack()));
        }
    }

    @Mixin(Blocks.class)
    public static class HarderBuddingAmethyst {
        @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "net/minecraft/block/BuddingAmethystBlock.<init> (Lnet/minecraft/block/AbstractBlock$Settings;)V", ordinal = 0), index = 0)
        private static AbstractBlock.Settings harderBudding(AbstractBlock.Settings settings)
        {
            if (!GoodMC.config.budding_hardness) return settings;
            return settings.resistance(1.5f).hardness(20f);
        }
    }

    // Client
    @Mixin(CompassAnglePredicateProvider.class)
    public abstract static class CompassAmethyst {
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
}
