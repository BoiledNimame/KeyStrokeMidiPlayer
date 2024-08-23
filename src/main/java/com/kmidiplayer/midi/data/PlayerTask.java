package com.kmidiplayer.midi.data;

import java.util.Arrays;

import com.kmidiplayer.application.UI;
import com.kmidiplayer.keylogger.IInputter;
import com.kmidiplayer.midi.MidiFilePlayer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class PlayerTask implements Runnable {

    private final MidiFilePlayer player;
    private final IInputter inputter;
    private final KeyCommand[][] iCommand;

    private int counter;
    private final long maxCount;

    private final User32 user32 = User32.INSTANCE;
    private final WinDef.HWND hWnd;

    public PlayerTask(IInputter inputter, MidiFilePlayer player, String windowTitle, KeyCommand[] inputComponent, int internalTick) {
        this.player = player;
        this.inputter = inputter;
        final KeyCommand[] commands = inputComponent;
        counter = 0;
        if (commands != null && commands[commands.length-1] != null) {
            maxCount = (Math.toIntExact(commands[commands.length-1].tick)/internalTick)+1;
        } else {
            UI.logger().warn("inputCompornent's length is 0 or null!");
            maxCount = 0;
        }
        hWnd = user32.FindWindow(null, windowTitle);

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
            UI.logger().info("Sequence is ended, stop Running this task.");
            player.cancel();
        }
        if (this.iCommand[counter].length != 0) {
            for (KeyCommand key : iCommand[counter]) {
                inputter.keyInput(user32, hWnd, key.isPush, key.vkCode);
            }
        }
        counter++;
    }
}
