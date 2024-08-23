package com.kmidiplayer.midi.multi;

import javax.sound.midi.Sequence;

import com.kmidiplayer.midi.data.KeyCommand;

public class MultiTrackMidiData {
    private final Sequence sequence;
    private int selectedIndex;

    public MultiTrackMidiData(Sequence sequence) {
        this.sequence = sequence;
    }

    public KeyCommand[] convert() {
        return MultiTrackMidiLoader.convert(selectedIndex, sequence);
    }

    public String[] getTrackInfo() {
        return MultiTrackMidiLoader.getTrackInfoFromSequence(sequence);
    }

    public void setSelectedTrackIndex(int index) {
        this.selectedIndex = index;
    }

    public long getTickMicroseconds() {
        return sequence.getMicrosecondLength()/sequence.getTickLength();
    }
}
