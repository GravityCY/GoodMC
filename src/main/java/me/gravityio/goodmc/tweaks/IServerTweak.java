package me.gravityio.goodmc.tweaks;

import net.minecraft.server.MinecraftServer;

@SuppressWarnings("ALL")
public interface IServerTweak {

    void onInit();

    @SuppressWarnings("EmptyMethod")
    void onServerStart(MinecraftServer server);

    @SuppressWarnings("EmptyMethod")
    void onTick();
}
