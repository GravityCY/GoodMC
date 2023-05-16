package me.gravityio.goodmc.lib.keybinds;

import me.gravityio.goodmc.lib.NullVisitor;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.Nullable;

public class KeyBind {
    public final KeyBinding bind;
    private NullVisitor whilePressedVisitor;
    private NullVisitor pressedVisitor;
    private NullVisitor releasedVisitor;

    protected boolean down;

    public KeyBind(KeyBinding bind) {
        this(bind, null, null);
    }

    public KeyBind(KeyBinding bind, NullVisitor whilePressedVisitor) {
        this(bind, whilePressedVisitor, null);
    }

    public KeyBind(KeyBinding bind, NullVisitor whilePressed, NullVisitor pressedVisitor) {
        this(bind, whilePressed, pressedVisitor, null);
    }

    public KeyBind(KeyBinding bind, @Nullable NullVisitor whilePressed, @Nullable NullVisitor pressedVisitor, @Nullable NullVisitor releasedVisitor) {
        this.bind = bind;
        this.whilePressedVisitor = whilePressed;
        this.pressedVisitor = pressedVisitor;
        this.releasedVisitor = releasedVisitor;
    }

    public void setWhilePressedCallback(NullVisitor visitor) {
        this.whilePressedVisitor = visitor;
    }

    public void setOnPressedCallback(NullVisitor visitor) {
        this.pressedVisitor = visitor;
    }

    public void setOnReleaseCallback(NullVisitor visitor) {
        this.releasedVisitor = visitor;
    }

    protected void whilePressed() {
        if (this.whilePressedVisitor == null) return;
        this.whilePressedVisitor.visit();
    }

    protected void onPressed() {
        if (this.pressedVisitor == null) return;
        this.pressedVisitor.visit();
    }

    protected void onRelease() {
        if (this.releasedVisitor == null) return;
        this.releasedVisitor.visit();
    }

    public static KeyBind of(String translationKey, int code, String category) {
        return of(translationKey, code, category, null, null);
    }
    public static KeyBind of(String translationKey, int code, String category, NullVisitor onPressed) {
        return of(translationKey, code, category, onPressed, null);
    }

    public static KeyBind of(String translationKey, int code, String category, NullVisitor onPressed, NullVisitor isPressed) {
        return new KeyBind(new KeyBinding(translationKey, code, category), onPressed, isPressed);
    }

    public static KeyBind of(String translationKey, int code, String category, NullVisitor whilePressed, NullVisitor onPressed, NullVisitor onRelease) {
        return new KeyBind(new KeyBinding(translationKey, code, category), whilePressed, onPressed, onRelease);
    }

}
