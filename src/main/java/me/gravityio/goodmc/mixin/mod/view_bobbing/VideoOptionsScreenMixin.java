package me.gravityio.goodmc.mixin.mod.view_bobbing;

import me.gravityio.goodmc.GoodMC;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.gravityio.goodmc.client.tweaks.ClientTweaks.VIEW_BOBBING;

@Mixin(VideoOptionsScreen.class)
public class VideoOptionsScreenMixin extends GameOptionsScreen {
    @Shadow private ButtonListWidget list;

    public VideoOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.list.addSingleOptionEntry(VIEW_BOBBING.viewBobbingStrength);
    }

    @Inject(method = "method_19865", at = @At("HEAD"))
    private void onSave(Window window, ButtonWidget button, CallbackInfo ci) {
        GoodMC.CONFIG.all.view_bobbing_strength = VIEW_BOBBING.viewBobbingStrength.getValue();
        GoodMC.CONFIG_HOLDER.save();
    }

}
