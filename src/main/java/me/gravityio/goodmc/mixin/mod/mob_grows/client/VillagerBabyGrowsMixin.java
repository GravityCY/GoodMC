package me.gravityio.goodmc.mixin.mod.mob_grows.client;

import me.gravityio.goodmc.GoodConfig;
import me.gravityio.goodmc.GoodConfig.AnimalAging.AgeMobOnly;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(VillagerEntityRenderer.class)
public abstract class VillagerBabyGrowsMixin extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {

    public VillagerBabyGrowsMixin(EntityRendererFactory.Context context, VillagerResemblingModel<VillagerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @ModifyConstant(method = "scale(Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V",
            constant = @Constant(floatValue = 0.5f, ordinal = 0))
    private float scaleSizeByAge(float g, VillagerEntity entity) {
        if (!GoodConfig.INSTANCE.aging.mob_aging || GoodConfig.INSTANCE.aging.only != AgeMobOnly.ALL && GoodConfig.INSTANCE.aging.only != AgeMobOnly.VILLAGER) return g;
        return 0.5f * (2 - (entity.getBreedingAge() / -24000f));
    }

    @ModifyConstant(method = "scale(Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V",
            constant = @Constant(floatValue = 0.25f, ordinal = 0))
    private float scaleShadowByAge(float g, VillagerEntity entity) {
        if (!GoodConfig.INSTANCE.aging.mob_aging || GoodConfig.INSTANCE.aging.only != AgeMobOnly.ALL && GoodConfig.INSTANCE.aging.only != AgeMobOnly.VILLAGER) return g;
        return 0.25f * (2 - (entity.getBreedingAge() / -24000f));
    }
}