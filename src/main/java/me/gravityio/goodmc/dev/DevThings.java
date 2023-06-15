package me.gravityio.goodmc.dev;

import me.gravityio.goodlib.dev.CommandProcessor;

public class DevThings {

    public static void init() {
        CommandProcessor.register(DevCommands.class);
    }

}
