package me.gravityio.goodmc.client.tweaks.todo_list;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class TodoItemChecker implements TodoChecker<TodoItemStack> {
    @Override
    public boolean isValid(Identifier id) {
        return Registries.ITEM.getOrEmpty(id).isPresent();
    }

    @Override
    public TodoItemStack instantiate(MinecraftClient client, Identifier id) {
        return new TodoItemStack(client, Registries.ITEM.get(id));
    }
}
