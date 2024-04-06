package com.kmidiplayer.midi.multi;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.gui.Gui;
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
        if (commands != null && commands[commands.length-1] != null) {
            maxCount = (Math.toIntExact(commands[commands.length-1].tick)/internalTick)+1;
        } else {
            Gui.logger().warn("MultitrackMidiData:: inputCompornent's length is 0 or null!");
            maxCount = 0;
        }
        hWnd = user32.FindWindow(null, ConfigHolder.instance().getWindowName());

        iCommand = new KeyCommand[(Math.toIntExact(commands[commands.length-1].tick)/internalTick)+1][];
        final KeyCommand[] EMPTY_KEYS_ARRAY = new KeyCommand[0];
        for (int i = 0; i < (commands[commands.length-1].tick/internalTick)+1; i++) {
            final int internalTickIndex = i;
            if (i==0) {
                if (Arrays.stream(commands)
                    .filter(cmd -> cmd.tick <= internalTick*(internalTickIndex)).count() != 0) {
                        this.iCommand[i] = Arrays.stream(commands)
                        .filter(cmd -> cmd.tick <= internalTick*(internalTickIndex))
                        .toArray(KeyCommand[]::new);
                } else {
                    this.iCommand[i] = EMPTY_KEYS_ARRAY;
                }
            } else {
                if (Arrays.stream(commands)
                    .filter(cmd -> cmd.tick <= internalTick*(internalTickIndex) && cmd.tick > internalTick*(internalTickIndex-1)).count()!=0) {
                        this.iCommand[i] = Arrays.stream(commands)
                        .filter(cmd -> cmd.tick <= internalTick*(internalTickIndex) && cmd.tick > internalTick*(internalTickIndex-1))
                        .toArray(KeyCommand[]::new);
                } else {
                    this.iCommand[i] = EMPTY_KEYS_ARRAY;
                }
            }
        }
    }

    @Override
    public void run() {
        if (maxCount < counter) {
            Gui.logger().info("Sequence is ended, stop Running this thread.");
            timer.cancel();
        }
        if (this.iCommand[counter].length != 0) {
            for (KeyCommand key : iCommand[counter]) {
                inputWrapper(key.isPush, key.vkCode);
            }
        }
        counter++;
    }

    private void inputWrapper(boolean isDown, int vkCode) {
        // inputter.keyInput(user32, hWnd, isDown, vkCode);
        inputter.mockedInput(user32, hWnd, isDown, vkCode);
    }
}
