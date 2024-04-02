package com.kmidiplayer.midi.multi;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        final Logger logger = LogManager.getLogger("MidiPlayer");
        // TODO なんもわからん
        for (KeyCommand cmd : keyInputComponent) {
            logger.debug("isPress:" + (cmd.isPush ? cmd.isPush + " " : cmd.isPush) + ", note:" + cmd.note + ", tick:" + cmd.tick + ", millis:" + ((cmd.tick * tickMicroseconds) / 1000));
        }
    }
}
