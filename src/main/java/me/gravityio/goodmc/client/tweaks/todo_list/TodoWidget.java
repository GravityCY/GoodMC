package me.gravityio.goodmc.client.tweaks.todo_list;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;

@SuppressWarnings("ALL")
public class TodoWidget extends EntryListWidget.Entry<TodoWidget> {

    private final MinecraftClient client;
    private final TextRenderer textRenderer;

    private static final int entryWidth = 50;
    private final Todo renderable;
    private final int count;
    private final int need;

    public TodoWidget(MinecraftClient client, Todo renderable, int count, int need) {
        this.client = client;
        this.renderable = renderable;
        this.count = count;
        this.need = need;
        this.textRenderer = this.client.textRenderer;
    }

    @Override
    public void render(MatrixStack stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        stack.push();
        int sx = x + 14;
        int ex = x + 75 - 8;
        int vx = (int) range(0, 1, sx, ex, (float)this.count / this.need);
        stack.scale(0.5f, 0.5f, 1);
        this.renderable.render(stack, x, y + entryHeight / 2);
        DrawableHelper.drawCenteredTextWithShadow(stack, textRenderer, this.count + " / " + this.need, 0, 0, this.count >= this.need ? 0xff00ff00 : 0xffff0000);
        DrawableHelper.fill(stack, sx, y + 22, vx, y + 23, 0xffffffff);
        stack.pop();
    }

    @SuppressWarnings("SameParameterValue")
    private float range(float min, float max, float tmin, float tmax, float value)
    {
        return (value - min) / (max - min) * (tmax - tmin) + tmin;
    }

}
