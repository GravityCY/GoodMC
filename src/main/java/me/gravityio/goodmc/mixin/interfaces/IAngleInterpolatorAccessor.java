package me.gravityio.goodmc.mixin.interfaces;

import net.minecraft.client.item.CompassAnglePredicateProvider.AngleInterpolator;

public interface IAngleInterpolatorAccessor {

    AngleInterpolator getInterpolator();
    void setInterpolator(AngleInterpolator interpolator);
}
