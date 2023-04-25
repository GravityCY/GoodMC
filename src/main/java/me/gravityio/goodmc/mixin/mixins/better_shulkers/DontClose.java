package me.gravityio.goodmc.mixin.mixins.better_shulkers;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SimpleNamedScreenHandlerFactory.class)
public abstract class DontClose implements NamedScreenHandlerFactory {
    @Override
    public boolean shouldCloseCurrentScreen() {
        return false;
    }
}