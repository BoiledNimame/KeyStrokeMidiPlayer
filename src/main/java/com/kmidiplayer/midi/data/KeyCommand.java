package com.kmidiplayer.midi.data;

public class KeyCommand {
    final boolean isPush;
    final long tick;
    public long getTick() { return tick; };
    final int vkCode;
    final int noteNumber;
    public KeyCommand(boolean isPush, long playTick, int vkCode, int noteNumber) {
        this.isPush = isPush;
        this.tick = playTick;
        this.vkCode = vkCode;
        this.noteNumber = noteNumber;
    }
}
