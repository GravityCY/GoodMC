package me.gravityio.goodmc.lib.better_compass;

public interface IMovementLocatable extends ILocatable {

    /**
     * This should return a boolean that will be used to determine whether to go through the players inventory to check for items and if they're {@link ILocatable#isLocatable}
     * @param distance The distance the player has moved since the last update
     * @param velocity The current velocity of the player
     * @return A boolean
     */
    boolean hasMoved(double distance, double velocity);

}
