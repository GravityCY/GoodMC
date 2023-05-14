package me.gravityio.goodmc.mixin.mod.better_shulkers;

import net.fabricmc.fabric.api.screenhandler.v1.FabricScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SimpleNamedScreenHandlerFactory.class)
public abstract class NoCloseCurrentScreenMixin implements FabricScreenHandlerFactory {
    @Override
    public boolean shouldCloseCurrentScreen() {
        return false;
    }
}