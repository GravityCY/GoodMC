package me.gravityio.goodmc.client.tweaks.todo_list;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@SuppressWarnings("ALL")
public class TodoWidget extends EntryListWidget.Entry<TodoWidget> {

    private final MinecraftClient client;
    private final ItemRenderer itemRenderer;
    private final TextRenderer textRenderer;

    private static final int entryWidth = 50;

    private final ItemStack itemStack;
    private final int count;
    private final int maxCount;

    public TodoWidget(MinecraftClient client, ItemStack itemStack, int count, int maxCount) {
        this.client = client;
        this.itemStack = itemStack;
        this.count = count;
        this.maxCount = maxCount;
        this.itemRenderer = this.client.getItemRenderer();
        this.textRenderer = this.client.textRenderer;
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        renderItem(this.itemStack, x, y + entryHeight / 2, 0.75f);
//        String strCount = String.valueOf(this.count);
//        String strSeparator = "/";
//        String strMaxCount = String.valueOf(this.maxCount);
//        int widthCount = textRenderer.getWidth(strCount);
//        int widthSeperator = textRenderer.getWidth(strSeparator);
//        int widthMaxCount = textRenderer.getWidth(strMaxCount);
//        renderText(strCount, x + entryWidth / 2 - widthMaxCount - widthSeperator, y, 1f);
//        renderText(strSeparator, x + 2, y, 1f);
        renderText(this.count + " / " + this.maxCount, x + entryWidth / 2 + 4, y + entryHeight / 2, this.count >= this.maxCount ? 0xff00ff00 : 0xffff0000);
        int sx = x + 14;
        int ex = x + 75 - 8;
        int vx = (int) range(0, 1, sx, ex, (float)this.count / this.maxCount);
        DrawableHelper.fill(matrices, sx, y + 22, vx, y + 23, 0xffffffff);
    }

    @SuppressWarnings("SameParameterValue")
    private float range(float min, float max, float tmin, float tmax, float value)
    {
        return (value - min) / (max - min) * (tmax - tmin) + tmin;
    }

    private void renderText(String text, int x, int y, int color)
    {
        MatrixStack stack = new MatrixStack();
        DrawableHelper.drawCenteredText(stack, textRenderer, text, x, y + 5, color);
    }

    @SuppressWarnings("SameParameterValue")
    private void renderItem(ItemStack itemStack, int x, int y, float scale)
    {
        MatrixStack stack = RenderSystem.getModelViewStack();
        stack.push();
        stack.translate(x, y, 100f);
        stack.translate(8, 8, 0);
        stack.scale(1, -1, 1);
        stack.scale(16, 16, 1);
        stack.scale(scale, scale, scale);
        RenderSystem.applyModelViewMatrix();
        MatrixStack stack2 = new MatrixStack();
        BakedModel model = itemRenderer.getModel(this.itemStack, null, null,  0);
        boolean notLit = !model.isSideLit();
        if (notLit) DiffuseLighting.disableGuiDepthLighting();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GUI, false, stack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (notLit) DiffuseLighting.enableGuiDepthLighting();
        stack.pop();
        RenderSystem.applyModelViewMatrix();
    }
}
