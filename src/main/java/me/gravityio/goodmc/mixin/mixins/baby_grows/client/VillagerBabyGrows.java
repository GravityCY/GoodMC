package me.gravityio.goodmc.mixin.mixins.baby_grows.client;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(VillagerEntityRenderer.class)
public abstract class VillagerBabyGrows extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {

    public VillagerBabyGrows(EntityRendererFactory.Context context, VillagerResemblingModel<VillagerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @ModifyConstant(allow = 1, method = "scale(Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V",
            constant = @Constant(floatValue = 0.5f, ordinal = 0))
    private float scaleSizeByAge(float g, VillagerEntity entity) {
        if (!GoodMC.config.animal_aging) return 0.5f;
        return 0.5f * (1 + (entity.age / 24000f));
    }

    @ModifyConstant(allow = 1, method = "scale(Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V",
            constant = @Constant(floatValue = 0.25f, ordinal = 0))
    private float scaleShadowByAge(float g, VillagerEntity entity) {
        if (!GoodMC.config.animal_aging) return 0.25f;
        return 0.25f * (1 + (entity.age / 24000f));
    }
}