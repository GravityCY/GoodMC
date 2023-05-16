package me.gravityio.goodmc.client;

import me.gravityio.goodmc.client.tweaks.ClientTweaks;
import me.gravityio.goodmc.client.tweaks.IClientTweak;
import me.gravityio.goodmc.lib.keybinds.KeybindManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class GoodClientMC implements ClientModInitializer {
    public static final String CATEGORY = "key.categories.goodmc";

    private MinecraftClient client;
    @Override
    public void onInitializeClient() {
        this.client = MinecraftClient.getInstance();

        ClientTweaks.tweaks.forEach(iTweak -> iTweak.onInit(this.client));
        KeybindManager.init();
        ClientTickEvents.END_CLIENT_TICK.register(client1 -> {
            ClientTweaks.tweaks.forEach(IClientTweak::onTick);
            KeybindManager.tick();
        });
    }
}
