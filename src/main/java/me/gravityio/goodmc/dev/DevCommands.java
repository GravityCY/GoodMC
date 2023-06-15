package me.gravityio.goodmc.dev;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.tweaks.locator.LocatorUtils;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.Map;

import static me.gravityio.goodlib.dev.CommandProcessor.*;

@Commands(namespace = "goodmc")
public class DevCommands {
    @Commands(namespace = "locator")
    public static class Locator {

        static {
            ArgumentTypeRegistry.registerArgumentType(new Identifier(GoodMC.MOD_ID, "poi"), PoiArgumentType.class, ConstantArgumentSerializer.of(PoiArgumentType::thingyMajingy));
        }

        private static class PoiArgumentType extends EnumArgumentType<PoiArgumentType.PoiType> {
            protected PoiArgumentType() {
                super(StringIdentifiable.createCodec(PoiType::values), PoiType::values);
            }

            public static PoiArgumentType thingyMajingy() {
                return new PoiArgumentType();
            }

            enum PoiType implements StringIdentifiable {
                BIOME, STRUCTURE;

                @Override
                public String asString() {
                    return this.toString();
                }
            }
        }

        @ArgumentHandler
        public static Map<Class<?>, IArgumentHandler<ServerCommandSource>> getArgumentHandlers(String cmdName) {
            var def = IArgumentHandler.getDefaultServerHandlers();
            def.put(ServerPlayerEntity.class, (name, context) -> context.getSource().getPlayer());
            def.put(PoiArgumentType.PoiType.class, (name, context) -> context.getArgument(name, PoiArgumentType.PoiType.class));
            return def;
        }

        @TypeProvider
        public static Map<Class<?>, IArgumentTypeProvider> getArgumentTypeProviders(String cmdName) {
            var def = IArgumentTypeProvider.getDefaultTypeProviders();
            def.put(PoiArgumentType.PoiType.class, PoiArgumentType::thingyMajingy);
            return def;
        }

        private static boolean isCompass(ServerPlayerEntity player, ItemStack compass) {
            if (!compass.isOf(Items.COMPASS)) {
                player.sendMessage(Text.literal("Please Select a Compass").formatted(Formatting.RED));
                return false;
            }
            return true;
        }

        @Command
        public static void craft(ServerPlayerEntity player, @Argument(name = "poi_type") PoiArgumentType.PoiType type) {
            ItemStack compass = player.getMainHandStack();
            if (!isCompass(player, compass)) return;
            if (type == PoiArgumentType.PoiType.BIOME) {
                LocatorUtils.Actions.onCraftBiome(player, compass);
            } else if (type == PoiArgumentType.PoiType.STRUCTURE) {
                LocatorUtils.Actions.onCraftStructure(player, compass);
            }
        }

        @Command
        public static void addUses(ServerPlayerEntity player, int add) {
            ItemStack compass = player.getMainHandStack();
            if (!isCompass(player, compass)) return;
            LocatorUtils.Data.addLocateUses(compass, add);
        }

        @Command
        public static void fillUses(ServerPlayerEntity player) {
            ItemStack compass = player.getMainHandStack();
            if (!isCompass(player, compass)) return;
            LocatorUtils.Data.setLocateUses(compass, LocatorUtils.Data.getLocateMaxUses(compass));
        }
    }
}
