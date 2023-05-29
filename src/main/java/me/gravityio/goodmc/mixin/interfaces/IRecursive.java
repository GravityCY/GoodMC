package me.gravityio.goodmc.mixin.interfaces;

public interface IRecursive {
    IRecursive getParent();
    void setParent();
}
