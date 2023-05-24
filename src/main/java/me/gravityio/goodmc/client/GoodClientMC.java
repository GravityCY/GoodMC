package me.gravityio.goodmc.client;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.client.tweaks.ClientTweaks;
import me.gravityio.goodmc.client.tweaks.IClientTweak;
import me.gravityio.goodmc.lib.keybinds.KeybindManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static me.gravityio.goodmc.GoodMC.MOD_ID;

public class GoodClientMC implements ClientModInitializer {
    public static final String CATEGORY = "key.categories.goodmc";

    private MinecraftClient client;
    @Override
    public void onInitializeClient() {
        GoodMC.LOGGER.info("[Client] Initializing...");
        this.client = MinecraftClient.getInstance();

        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MOD_ID, "compass"), modContainer, Text.of("3D Compass"), ResourcePackActivationType.DEFAULT_ENABLED);

        });

        GoodMC.LOGGER.debug("[Client] Initializing all tweaks.." );
        ClientTweaks.tweaks.forEach(iTweak -> {
            GoodMC.LOGGER.debug("[Client] Initializing Tweak: '{}'...", iTweak.getClass().getSimpleName());
        });
        ClientTweaks.tweaks.forEach(iTweak -> {
            iTweak.onInit(this.client);
            GoodMC.LOGGER.debug("[Client] Initialized Tweak: '{}'!", iTweak.getClass().getSimpleName());

        });
        KeybindManager.init();
        ClientTickEvents.END_CLIENT_TICK.register(client1 -> {
            ClientTweaks.tweaks.forEach(IClientTweak::onTick);
            KeybindManager.tick();
        });
        GoodMC.LOGGER.info("[Client] Initialized.");
    }
}
