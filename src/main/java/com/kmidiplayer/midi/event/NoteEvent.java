package com.kmidiplayer.midi.event;

import com.kmidiplayer.midi.data.KeyCommand;

public class NoteEvent {

    final boolean isPush;
    final int noteNumber;

    public NoteEvent(KeyCommand cmd) {
        isPush = cmd.isPush();
        noteNumber = cmd.getNoteNumber();
    }

    public boolean isPushed() { return isPush; }
    public int getNoteNumber() { return noteNumber; }
}