package me.gravityio.goodmc.client.tweaks;

import net.minecraft.client.MinecraftClient;

public interface IClientTweak {
    void onInit(MinecraftClient client);
    void onTick();
}
