package me.gravityio.goodmc.client.tweaks.todo_list;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public interface TodoChecker<T extends Todo> {
    boolean isValid(Identifier id);
    T instantiate(MinecraftClient client, Identifier id);
}
