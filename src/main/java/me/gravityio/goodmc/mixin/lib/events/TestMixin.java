package me.gravityio.goodmc.mixin.lib.events;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class TestMixin {
  @Inject(method = "onClickSlot", at = @At("HEAD"))
  private void onClickSlotEvent(ClickSlotC2SPacket packet, CallbackInfo ci) {
//    GoodMC.LOGGER.debug("TEST MOMENT");
  }
}
