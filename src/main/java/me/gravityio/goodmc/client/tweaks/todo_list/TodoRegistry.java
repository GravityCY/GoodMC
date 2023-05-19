package me.gravityio.goodmc.client.tweaks.todo_list;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TodoRegistry {
    public static final List<TodoEntry> todoList = new ArrayList<>();
    public static final List<TodoChecker<? extends Todo>> todoCheckers = new ArrayList<>();
    private static MinecraftClient client;

    static {
        registerChecker(new TodoItemChecker());
    }

    public static void setClient(MinecraftClient client) {
        TodoRegistry.client = client;
    }

    public static boolean isValid(Identifier id) {
        return getFromIdentifier(id) != null;
    }

    public static void registerChecker(TodoChecker<? extends Todo> checker) {
        todoCheckers.add(checker);
    }

    public static TodoChecker<? extends Todo> getFromIdentifier(Identifier identifier) {
        for (TodoChecker<? extends Todo> todoChecker : todoCheckers) {
            if (todoChecker.isValid(identifier)) return todoChecker;
        }
        return null;
    }

    public static boolean add(Identifier identifier, int need) {
        TodoChecker<? extends Todo> checker = getFromIdentifier(identifier);
        if (checker == null) return false;
        Todo todo = checker.instantiate(client, identifier);
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
