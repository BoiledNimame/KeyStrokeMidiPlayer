package com.kmidiplayer.midi.data;

public class KeyCommand {
    final boolean isPush;
    final long tick;
    final int vkCode;
    public KeyCommand(boolean isPush, long playTick, int vkCode) {
        this.isPush = isPush;
        this.tick = playTick;
        this.vkCode = vkCode;
    }
}
