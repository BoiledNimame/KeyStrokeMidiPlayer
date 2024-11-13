package com.kmidiplayer.keylogger;

import java.awt.AWTException;
import java.awt.Robot;

public class KeyboardRobot implements IInputter {

    private final Robot robot;

    KeyboardRobot() {
        try {
            robot = new Robot();
            robot.setAutoDelay(0);
            robot.setAutoWaitForIdle(false);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void keyInput(String windowName, boolean isDown, int vkCode) {
        if (isDown) {
            robot.keyPress(vkCode-7);
            return;
        } else {
            robot.keyRelease(vkCode-7);
            return;
        }
    }
}
