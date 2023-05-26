package me.gravityio.goodmc.client.tweaks.todo_list;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class TodoItemStack implements Todo {
    private final MinecraftClient client;
    private final ItemStack item;

    public TodoItemStack(MinecraftClient client, Item item) {
        this.client = client;
        this.item = new ItemStack(item);
    }

    @Override
    public void render(MatrixStack stack, int x, int y) {
        ItemRenderer itemRenderer = client.getItemRenderer();
        MatrixStack viewStack = RenderSystem.getModelViewStack();
        viewStack.push();
        viewStack.translate(x, y + 8, 100f);
        viewStack.scale(1, -1, 1);
        viewStack.scale(16, 16, 1);
        RenderSystem.applyModelViewMatrix();
        BakedModel model = itemRenderer.getModel(this.item, null, null,  0);
        boolean notLit = !model.isSideLit();
        if (notLit) DiffuseLighting.disableGuiDepthLighting();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        itemRenderer.renderItem(item, ModelTransformation.Mode.GUI, false, stack, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (notLit) DiffuseLighting.enableGuiDepthLighting();
        viewStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

    @Override
    public Identifier getId() {
        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(this.item.getItem());
        return key.map(RegistryKey::getValue).orElse(null);
    }
}
