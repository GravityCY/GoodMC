package me.gravityio.goodmc.mixin.mod.better_budding;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Blocks.class)
public class HarderBuddingAmethyst {
    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/block/BuddingAmethystBlock.<init> (Lnet/minecraft/block/AbstractBlock$Settings;)V",
                    ordinal = 0
            ),
            index = 0
    )
    private static AbstractBlock.Settings harderBudding(AbstractBlock.Settings settings)
    {
        if (!GoodMC.config.budding_hardness) return settings;
        return settings.resistance(1.5f).hardness(20f);
    }
}