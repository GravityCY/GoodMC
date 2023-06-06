package me.gravityio.goodmc.client.tweaks.todo_list;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TodoRegistry {
    public static final List<TodoEntry> todoList = new ArrayList<>();
    public static final List<TodoFactory<? extends Todo>> TODO_FACTORIES = new ArrayList<>();
    private static MinecraftClient client;

    static {
        registerChecker(new TodoItemFactory());
    }

    public static void setClient(MinecraftClient client) {
        TodoRegistry.client = client;
    }

    public static boolean isValid(Identifier id) {
        return getFromIdentifier(id) != null;
    }

    public static void registerChecker(TodoFactory<? extends Todo> checker) {
        TODO_FACTORIES.add(checker);
    }

    public static TodoFactory<? extends Todo> getFromIdentifier(Identifier identifier) {
        for (TodoFactory<? extends Todo> todoFactory : TODO_FACTORIES) {
            if (todoFactory.isValid(identifier)) return todoFactory;
        }
        return null;
    }

    public static boolean add(Identifier identifier, int need) {
        TodoFactory<? extends Todo> checker = getFromIdentifier(identifier);
        if (checker == null) return false;
        Todo todo = checker.create(client, identifier);
        todoList.add(new TodoEntry(todo, 0, need));
        return true;
    }

    public static boolean remove(Identifier id) {
        for (int i = 0; i < todoList.size(); i++) {
            if (todoList.get(i).item.getId().equals(id)) {
                todoList.remove(i);
                return true;
            }
        }
        return false;
    }

    public static boolean remove(int index) {
        return todoList.remove(index) != null;
    }

}
