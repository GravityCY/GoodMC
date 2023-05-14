package me.gravityio.goodmc.mixin.mod.client;

import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes the smithing screen remove the little ingot icon when the slot is occupied <br>
 * When using anything other than an ingot in the second slot it just look kinda weirdy <br>
 */
@Mixin(ForgingScreen.class)
public abstract class BetterSmithingScreenMixin<T extends ForgingScreenHandler> extends HandledScreen<T> {

    public BetterSmithingScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    private void onRenderBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        ForgingScreen<T> self = (ForgingScreen<T>) (Object) this;
        if (!(self instanceof SmithingScreen)) return;
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        if (!this.handler.getSlot(1).hasStack())
            this.drawTexture(matrices, i+76, j+49, 181, 23, 16, 12);
    }
}
