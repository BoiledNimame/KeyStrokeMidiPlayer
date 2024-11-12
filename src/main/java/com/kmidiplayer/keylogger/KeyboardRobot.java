package com.kmidiplayer.keylogger;

import java.awt.AWTException;
import java.awt.Robot;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

public class KeyboardRobot implements IInputter {

    private final Robot robot;
    
    KeyboardRobot() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void keyInput(User32 user32, HWND hWnd, boolean isDown, int vkCode) {
        if (isDown) {
            robot.keyPress(vkCode);
        } else {
            robot.keyRelease(vkCode);
        }
    }
}
