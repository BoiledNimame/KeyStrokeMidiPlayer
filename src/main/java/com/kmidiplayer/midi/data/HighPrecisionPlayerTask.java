package com.kmidiplayer.midi.data;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.keylogger.IInputter;
import com.kmidiplayer.midi.event.INoteEventListener;
import com.kmidiplayer.midi.event.NoteEvent;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class HighPrecisionPlayerTask implements Runnable {

    private final static Logger LOGGER = LogManager.getLogger("[H.Player]");

    private final Runnable stopper;
    private final List<INoteEventListener> listeners;

    private final IInputter inputter;
    private final KeyCommand[] commands;

    private int currentTick;
    private int currentIndex;

    private final long maxTick;

    private final User32 user32 = User32.INSTANCE;
    private final WinDef.HWND hWnd;

    public HighPrecisionPlayerTask (IInputter inputter, String windowTitle, KeyCommand[] inputCommands, Runnable stopper, List<INoteEventListener> listeners) {

        Objects.requireNonNull(stopper);
        this.stopper = stopper;
        this.listeners = listeners;

        if (inputCommands == null) {
            throw new IllegalArgumentException("inputComponent is null!");
        } else if (inputCommands.length == 0) {
            throw new IllegalArgumentException("inputComponent's length is 0!");
        }

        this.inputter = inputter;
        this.commands = inputCommands;
        this.maxTick = inputCommands[inputCommands.length - 1].tick;

        hWnd = user32.FindWindow(null, windowTitle);
    }

    @Override
    public void run() {
        if (maxTick <= currentTick) {
            LOGGER.info("Sequence completed, stop execution of this task.");
            stopper.run();
        } else {
            if (currentIndex < commands.length && commands[currentIndex].tick <= currentTick) {
                while (currentTick == commands[currentIndex].tick) {
                    inputter.keyInput(user32, hWnd, commands[currentIndex].isPush, commands[currentIndex].vkCode);
                    listeners.forEach(l -> l.fire(new NoteEvent(commands[currentIndex])));
                    currentIndex++;
                }
            }
        }
        currentTick++;
    }
}
