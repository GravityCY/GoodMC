package me.gravityio.goodmc.client.tweaks.todo_list;

public class TodoEntry {
    public Todo item;
    public int count;
    public int need;

    public TodoEntry(Todo item, int count, int need) {
        this.item = item;
        this.count = count;
        this.need = need;
    }
}