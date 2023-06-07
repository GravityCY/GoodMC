package me.gravityio.goodmc.client.tweaks.todo_list;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.gravityio.goodlib.lib.keybinds.KeybindManager;
import me.gravityio.goodlib.lib.keybinds.KeybindWrapper;
import me.gravityio.goodmc.client.tweaks.IClientTweak;
import me.gravityio.goodmc.client.tweaks.todo_list.gui.TodoListWidget;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static me.gravityio.goodmc.client.GoodClientMC.CATEGORY;

/**
 * A TodoList GUI that I eventually make xd
 */
@SuppressWarnings("ALL")
public class TodoListTweak implements IClientTweak {

    private static final MutableText ADD_SUCCESS = Text.literal("Succesfully Added Todo.").formatted(Formatting.GREEN);
    private static final MutableText REMOVE_SUCCESS = Text.literal("Succesfully Removed Todo.").formatted(Formatting.RED);
    private static final MutableText REMOVE_FAILED = Text.literal("Failed to Remove Todo.").formatted(Formatting.RED);
    private static final MutableText INVALID_ID = Text.literal("Invalid ID.").formatted(Formatting.RED);

    public boolean doRender = true;

    private MinecraftClient client;
    private TodoListWidget todoWidget;
    private final LiteralArgumentBuilder<FabricClientCommandSource> todoCommand = ClientCommandManager.literal("todo")
            .then(ClientCommandManager.literal("add")
                            .then(ClientCommandManager.argument("id", IdentifierArgumentType.identifier())
                                    .then(ClientCommandManager.argument("need", IntegerArgumentType.integer(1, 64))
                                            .executes(context -> {
                                                Identifier id = context.getArgument("id", Identifier.class);
                                                int need = context.getArgument("need", Integer.class);
                                                if (!TodoRegistry.isValid(id)) {
                                                    context.getSource().getPlayer().sendMessage(INVALID_ID);
                                                    return -1;
                                                }
                                                TodoRegistry.add(id, need);
                                                return 1;
                                            })
                                    )
                            )
            )
            .then(ClientCommandManager.literal("remove")
                    .then(ClientCommandManager.argument("id", IdentifierArgumentType.identifier())
                            .executes(context -> {
                                Identifier id = context.getArgument("id", Identifier.class);
                                if (!TodoRegistry.isValid(id)) {
                                    context.getSource().getPlayer().sendMessage(INVALID_ID);
                                    return -1;
                                }
                               if (TodoRegistry.remove(id))
                                   context.getSource().getPlayer().sendMessage(REMOVE_SUCCESS);
                                else
                                   context.getSource().getPlayer().sendMessage(REMOVE_FAILED);
                                return 1;
                            })
                    )
            );

    @Override
    public void onInit(MinecraftClient client) {
        this.client = client;
        TodoRegistry.setClient(client);
        TodoRegistry.add(new Identifier("minecraft:stone"), 64);
        KeybindManager.register(KeybindWrapper.of("key.goodmc.todo", GLFW.GLFW_KEY_Y, CATEGORY,  () -> doRender = !doRender));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(todoCommand));
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            if (!doRender || TodoRegistry.todoList.isEmpty()) return;
            this.todoWidget = new TodoListWidget(client);
            todoWidget.render(matrices, 0, 0, tickDelta);
        });
    }

    @Override
    public void onTick() {
    }


}
