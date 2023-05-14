package me.gravityio.goodmc;

import me.gravityio.goodmc.tweaks.IServerTweak;
import me.gravityio.goodmc.tweaks.ServerTweaks;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Main Class of the mod, this will run on both the client and the server
 */
//  TODO: Make the compass registry compatible with structures / biomes and maybe pois
//  TODO: Make the better loot registry customizable and figure out how to register items to add on top of the loot table and not be tied to a loot table but just the whole structure
//  TODO: Make the distance when updating compasses configurable
//  TODO: Make custom recipe registration modular so that you can add custom recipes from a registry and maybe even custom recipes' other than just the smithing
//  TODO: Make the Todo List Tweak
//  TODO: Improve config backend
//  TODO: Configurable View Bobbing Strength
//  TODOTHINK: Try and fix the way animals look when visually growing up
//  TODOTHINK: Make it so that you can filter what the player will get when they roll a structure based on a list of what the player has already rolled?
//  TODOTHINK: Change the way the dimension of the roll gets decided (currently based on which dimension the player is during rolling)
//  THINK: Get rid of the arm renderables' and replace them with the fabric custom renderers
//  DONE: Send age of mobs to the client
//  DONE: Optimize the way I'm checking if chests' structure has already been looted
//  DONE: Need to make a better way of making custom loot tables that only spawn an item in a whole structure once, even if other inventories use the same lootable
//  DONE: Scale the distance by the velocity of the player
//  DONE: Make compasses more customizable
//  DONE: Make logs that appear only durring debugging
//  DONE: Structure Finding

@SuppressWarnings("ALL")
public class GoodMC implements ModInitializer {

    static {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }

    public static final String MOD_ID = "goodmc";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

    @Override
    public void onInitialize() {
        GoodMC.LOGGER.info(GoodMC.MOD_ID + " has been Initialized." );
        GoodMC.LOGGER.debug(GoodMC.MOD_ID + " has been set to Debug Mode." );
        ServerTweaks.tweaks.forEach(IServerTweak::onInit);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ServerTweaks.tweaks.forEach(iServerTweak -> iServerTweak.onServerStart(server)));
        ServerTickEvents.END_SERVER_TICK.register(server -> ServerTweaks.tweaks.forEach(IServerTweak::onTick));
    }
}
