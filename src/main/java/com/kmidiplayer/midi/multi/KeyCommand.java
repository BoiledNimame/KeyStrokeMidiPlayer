package com.kmidiplayer.midi.multi;

public class KeyCommand {
    final boolean isPush;
    final long tick;
    final int note;
    public KeyCommand(boolean isPush, long playTick, int note) {
        this.isPush = isPush;
        this.tick = playTick;
        this.note = note;
    }

    public boolean isPush() {
        return isPush;
    }

    public long tick() {
        return tick;
    }

    public int note() {
        return note;
    }
}
