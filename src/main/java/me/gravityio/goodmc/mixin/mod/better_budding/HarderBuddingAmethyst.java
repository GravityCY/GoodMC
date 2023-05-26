package me.gravityio.goodmc.mixin.mod.better_budding;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.gravityio.goodmc.GoodConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BuddingAmethystBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class HarderBuddingAmethyst {

    @ModifyReturnValue(method = "getHardness", at = @At("RETURN"))
    private float getHardness(float original)
    {
        AbstractBlock.AbstractBlockState self = (AbstractBlock.AbstractBlockState) (Object) this;
        if (!(self.getBlock() instanceof BuddingAmethystBlock)) return original;
        return GoodConfig.INSTANCE.amethyst.budding_hardness ? 20 : original;
    }
}