package me.gravityio.goodmc.client.tweaks.todo_list;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

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
