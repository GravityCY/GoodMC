package me.gravityio.goodmc.client.tweaks.wiki_tweak;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.client.tweaks.IClientTweak;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;

/**
 * A Tweak that adds a client sided command that opens the minecraft fandom website with whatever you type as arguments
 */
@SuppressWarnings("ALL")
public class WikiClientTweak implements IClientTweak {

    public final LiteralArgumentBuilder<FabricClientCommandSource> openWikiCmd = ClientCommandManager.literal("wiki").then(ClientCommandManager.argument("id", StringArgumentType.greedyString()).executes(context -> {
        String id = context.getArgument("id", String.class);
        openWiki(id);
        return 1;
    }));

    public void openWiki(String id)
    {
        try {
            String url = "https://minecraft.fandom.com/wiki/Special:Search?query=" + id;
            Runtime rn = Runtime.getRuntime();
            rn.exec("rundll32 url.dll,FileProtocolHandler " + url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInit(MinecraftClient client) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(openWikiCmd));
    }

    @Override
    public void onTick() {

    }
}
