package me.gravityio.goodmc.mixin.mod.better_budding;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.block.Block;
import net.minecraft.block.BuddingAmethystBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BuddingAmethystBlock.class)
public class BuddingAmethystBlockDrop extends Block {

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