package com.kmidiplayer.midi.multi;

import java.util.List;

import com.kmidiplayer.keylogger.KeyboardInput;

public class MultiTrackMidiPlayer extends Thread {
    private final KeyboardInput kInput;
    private final List<KeyCommand> keyInputComponent;
    private final long tickMicroseconds;
    private int advancedDelayMilliseconds;

    public MultiTrackMidiPlayer(KeyboardInput inputter, List<KeyCommand> keys, long microsecondsOf1tick) {
        kInput = inputter;
        keyInputComponent = keys;
        tickMicroseconds = microsecondsOf1tick;
    }

    public void addAdvanceDelay(int Milliseconds) {
        advancedDelayMilliseconds = Milliseconds;
    }

    @Override
    public void run() {
        // TODO なんもわからん
    }
}
