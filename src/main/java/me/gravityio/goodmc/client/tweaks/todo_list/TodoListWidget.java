package me.gravityio.goodmc.client.tweaks.todo_list;

import me.gravityio.goodmc.client.tweaks.ClientTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static me.gravityio.goodmc.client.tweaks.ClientTweaks.TODO_LIST;

@SuppressWarnings("ALL")
public class TodoListWidget extends EntryListWidget<TodoWidget> {

    public static final int entryWidth = 50;
    public static final int width = 75;
    public static final int height = 100;
    public static final int margin = 15;

    public TodoListWidget(MinecraftClient client) {
        super(client, width, height, client.getWindow().getScaledHeight() / 2 - height / 2, client.getWindow().getScaledHeight() / 2 + height / 2, 20);
        TodoRegistry.todoList.forEach(todo -> addEntry(new TodoWidget(super.client, todo.item, todo.count, todo.need)));
        super.setLeftPos(client.getWindow().getScaledWidth() - width - margin);
        super.setRenderBackground(false);
        super.setRenderHeader(false, 0);
        super.setRenderHorizontalShadows(false);
        super.setRenderSelection(false);
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected void renderBackground(MatrixStack matrices) {
//        DrawableHelper.fill(matrices, this.left, this.top, this.right, this.bottom, 0x55333333);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.HINT, Text.of("bruh"));
    }
}
