package me.gravityio.goodmc.client.tweaks.todo_list;

import net.minecraft.item.ItemStack;

public class TodoItem {
    public ItemStack item;
    public int count;
    public int need;

    public TodoItem(ItemStack item, int count, int need) {
        this.item = item;
        this.count = count;
        this.need = need;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNeed() {
        return need;
    }

    public void setNeed(int need) {
        this.need = need;
    }
}