package me.gravityio.goodmc;

import me.gravityio.goodmc.config.ModConfig;
import me.gravityio.goodmc.tweaks.IServerTweak;
import me.gravityio.goodmc.tweaks.ServerTweaks;
import me.gravityio.goodmc.tweaks.better_shulkers.BetterShulkersRegister;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.util.logging.Logger;

/**
 * The Main Class of the mod, this will run on both the client and the server
 */
@SuppressWarnings("ALL")
public class GoodMC implements ModInitializer {

    static {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }

    public static final String MOD_ID = "goodmc";
    public static Logger LOGGER = Logger.getLogger("goodmc");
    public static final ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

    @Override
    public void onInitialize() {
        ServerTweaks.tweaks.forEach(IServerTweak::onInit);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ServerTweaks.tweaks.forEach(iServerTweak -> iServerTweak.onServerStart(server)));
        ServerTickEvents.END_SERVER_TICK.register(server -> ServerTweaks.tweaks.forEach(IServerTweak::onTick));
        initRegistry();
    }

    private void initRegistry() {
        for (BetterShulkersRegister shulkerRegistryItem : FabricLoader.getInstance().getEntrypoints(MOD_ID, BetterShulkersRegister.class)) {
            shulkerRegistryItem.generate();
        }
    }
}
