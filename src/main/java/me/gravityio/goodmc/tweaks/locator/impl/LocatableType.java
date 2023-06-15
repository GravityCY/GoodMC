package me.gravityio.goodmc.tweaks.locator.impl;

public class LocatableType<T extends ILocatable> {
    public static final LocatableType<ILocatable> GENERIC_LOCATABLE = new LocatableType<>();
    public static final LocatableType<ILocatablePoi> POI_LOCATABLE = new LocatableType<>();

}
