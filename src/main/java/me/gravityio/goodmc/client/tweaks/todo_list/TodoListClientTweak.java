package me.gravityio.goodmc.client.tweaks.todo_list;

import me.gravityio.goodmc.client.tweaks.IClientTweak;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@SuppressWarnings("ALL")
public class TodoListClientTweak implements IClientTweak {

    KeyBinding left = new KeyBinding("test", GLFW.GLFW_KEY_LEFT_BRACKET, KeyBinding.MISC_CATEGORY);
    KeyBinding right = new KeyBinding("test2", GLFW.GLFW_KEY_RIGHT_BRACKET, KeyBinding.MISC_CATEGORY);

    public final List<Todo> todoList = List.of(
            new Todo(Items.STONE.getDefaultStack(), 1, 64),
            new Todo(Items.DIAMOND.getDefaultStack(), 0, 10),
            new Todo(Items.TORCH.getDefaultStack(), 0, 10));

    private MinecraftClient client;

    @Override
    public void onInit(MinecraftClient client) {
//        this.client = client;
//        KeyBindingHelper.registerKeyBinding(left);
//        KeyBindingHelper.registerKeyBinding(right);
//        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
//            TodoListWidget widget = new TodoListWidget(client);
//            widget.render(matrices, 0, 0, tickDelta);
//        });
    }

    @Override
    public void onTick() {
//        while (left.wasPressed()) {
//            todoList.forEach(todo -> todo.count--);
//        }
//        while (right.wasPressed())
//        {
//            todoList.forEach(todo -> todo.count++);
//        }
    }

    public static class Todo {
        public final ItemStack item;
        public final int count;
        public final int need;

        public Todo(ItemStack item, int count, int need) {
            this.item = item;
            this.count = count;
            this.need = need;
        }
    }
}
