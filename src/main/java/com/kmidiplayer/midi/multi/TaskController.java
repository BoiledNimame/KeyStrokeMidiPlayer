package com.kmidiplayer.midi.multi;

import java.util.Timer;
import java.util.TimerTask;

import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.keylogger.KeyboardInput;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class TaskController extends TimerTask {
    private final Timer timer;
    private final KeyboardInput inputter;
    private final KeyCommand[][] iCommand;

    private int counter;
    private final long maxCount;

    private final User32 user32 = User32.INSTANCE;
    private final WinDef.HWND hWnd;

    public TaskController(KeyboardInput inputter, Timer excuteTimer, KeyCommand[] inputComponent, int internalTick) {
        timer = excuteTimer;
        this.inputter = inputter;
        final KeyCommand[] commands = inputComponent;
        counter = 0;
        if (inputComponent != null) {
            maxCount = inputComponent[commands.length].tick;
        } else {
            maxCount = 0;
        }
        hWnd = user32.FindWindow(null, ConfigHolder.instance().getWindowName());
        
        // index
        int convertIndexFirst = 0;
        int convertIndexSecond = 0;
        int beforeIndex = 0;

        iCommand = new KeyCommand[(Math.toIntExact(inputComponent[commands.length].tick)/internalTick)+1][];
        for (int i = 0; i < commands.length; i++) {
            if(internalTick*(convertIndexFirst + 1) == i || i == commands.length) {
                convertIndexFirst++;
                convertIndexSecond = 0;
            }
            if (commands[beforeIndex].tick <= internalTick*(convertIndexFirst + 1) && commands[beforeIndex] != null) {
                iCommand[convertIndexFirst][convertIndexSecond] = commands[beforeIndex];
                convertIndexSecond++;
            }
        }
    }

    @Override
    public void run() {
        if (counter < maxCount) {
            timer.cancel();
        }
        if (iCommand[counter].length != 0) {
            for (KeyCommand key : iCommand[counter]) {
                inputWrapper(key.isPush, key.note);
            }
        }
        counter++;
    }

    private void inputWrapper(boolean isDown, int vkCode) {
        inputter.keyInput(user32, hWnd, isDown, vkCode);
    }
}
