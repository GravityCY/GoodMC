package me.gravityio.goodmc.mixin.lib.events;

import me.gravityio.goodmc.lib.events.ModEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "createWorlds", at = @At("TAIL"))
    private void afterCreatedWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        ModEvents.ON_CREATE_WORLDS.invoker().onCreateWorlds((MinecraftServer) (Object) this);
    }
}
