package me.gravityio.goodmc.mixin.lib;

import com.google.common.collect.ImmutableList;
import me.gravityio.goodmc.lib.events.ModEvents;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(TranslatableTextContent.class)
public abstract class MissingTranslationImplMixin {

    @Shadow private List<StringVisitable> translations;

    @Shadow @Final private String key;

    @Shadow private @Nullable Language languageCache;

    @Shadow protected abstract void forEachPart(String translation, Consumer<StringVisitable> partsConsumer);

    @Inject(method = "updateTranslations", at = @At(value = "FIELD", target = "Lnet/minecraft/text/TranslatableTextContent;translations:Ljava/util/List;", shift = At.Shift.AFTER))
    private void getTranslations(CallbackInfo ci) {
        if (!this.languageCache.get(this.key).equals(this.key)) return;
        StringBuilder sb = new StringBuilder();
        ImmutableList.Builder<StringVisitable> newText = new ImmutableList.Builder<>();
        ModEvents.ON_MISSING_TRANSLATION.invoker().onMissingTranslation(this.key, sb);
        String newString = sb.toString();
        if (newString.equals("")) return;
        this.forEachPart(newString, newText::add);
        this.translations = newText.build();
    }

}
