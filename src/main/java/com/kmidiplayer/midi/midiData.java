package com.kmidiplayer.midi;

import java.util.List;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;

public class midiData {
    private final List<long[]> playableKeyArr;
    public midiData(String midiDirectory) {
        final Sequence sequence = midiLoader.getSequencefromDirectory(midiDirectory);
        final List<MidiEvent> rawEvent = midiLoader.convertSequenceToMidiEvent(sequence);
        playableKeyArr = midiLoader.convertRawKeys(rawEvent);
    }
    public List<long[]> getplayableKeyArr() {
        return playableKeyArr;
    }
}
