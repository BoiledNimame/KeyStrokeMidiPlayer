package com.kmidiplayer.keylogger;

import com.kmidiplayer.config.Options;

public class InputterSupplier {
    public static IInputter getInstance() {
        if (Options.configs.getIsMock()) {
            return new KeyboardMock();
        } else if (Options.configs.useRobot()) {
            return new KeyboardRobot();
        } else {
            return new KeyboardInput();
        }
    }
}
