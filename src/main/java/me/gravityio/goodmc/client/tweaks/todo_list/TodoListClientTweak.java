package me.gravityio.goodmc.client.tweaks.todo_list;

import me.gravityio.goodmc.client.tweaks.IClientTweak;
import me.gravityio.goodmc.lib.keybinds.KeyBind;
import me.gravityio.goodmc.lib.keybinds.KeybindManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static me.gravityio.goodmc.client.GoodClientMC.CATEGORY;

/**
 * A TodoList GUI that I eventually make xd
 */
@SuppressWarnings("ALL")
public class TodoListClientTweak implements IClientTweak {
    public boolean doRender = false;
    private MinecraftClient client;
    public final List<TodoItem> todoList = List.of(
            new TodoItem(Items.STONE.getDefaultStack(), 1, 64),
            new TodoItem(Items.DIAMOND.getDefaultStack(), 0, 10),
            new TodoItem(Items.TORCH.getDefaultStack(), 0, 10)
    );

    @Override
    public void onInit(MinecraftClient client) {
        this.client = client;
        KeybindManager.register(KeyBind.of("key.goodmc.todo", GLFW.GLFW_KEY_Y, CATEGORY,  () -> doRender = !doRender));
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            if (!doRender || todoList.isEmpty()) return;
            TodoListWidget widget = new TodoListWidget(client);
            widget.render(matrices, 0, 0, tickDelta);
        });
    }

    @Override
    public void onTick() {
    }


}
