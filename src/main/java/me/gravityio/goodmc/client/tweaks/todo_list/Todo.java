package me.gravityio.goodmc.client.tweaks.todo_list;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public interface Todo {
    void render(MatrixStack stack, int x, int y);
    Identifier getId();
}
