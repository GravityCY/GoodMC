package me.gravityio.goodmc.client.tweaks.view_bobbing;

import me.gravityio.goodmc.GoodMC;
import me.gravityio.goodmc.client.tweaks.IClientTweak;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class ViewBobbingTweak implements IClientTweak {
    public final SimpleOption<Integer> viewBobbingStrength = new SimpleOption<>(
            "options.goodmc.bobbing_strength",
            SimpleOption.emptyTooltip(),
            (optionText, value) -> GameOptions.getGenericValueText(optionText, Text.translatable("options.goodmc.bobbing", value)),
            new SimpleOption.ValidatingIntSliderCallbacks(0, 100), 100, value -> {}
        );

    @Override
    public void onInit(MinecraftClient client) {
        GoodMC.CONFIG_HOLDER.registerSaveListener((configHolder, modConfig) -> {
            GoodMC.LOGGER.debug("<ViewBobbingTweak> Setting new VIEW_BOBBING_STRENGTH to {}", modConfig.all.view_bobbing_strength);
            viewBobbingStrength.setValue(modConfig.all.view_bobbing_strength);
            return ActionResult.SUCCESS;
        });
    }

    @Override
    public void onTick() {

    }
}
