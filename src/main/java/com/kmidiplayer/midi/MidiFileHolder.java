package com.kmidiplayer.midi;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.midi.multi.MultiTrackMidiPlayer;
import com.kmidiplayer.midi.util.MidiFileChecker;
import com.kmidiplayer.midi.util.NoteConverter;

public class MidiFileHolder {

    private final Sequence sequence;

    public MidiFileHolder(File file) {
        if (MidiFileChecker.isValid(file)) {
            try {
                sequence = MidiSystem.getSequence(file);
            } catch (InvalidMidiDataException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            sequence = null;
        }
    }

    public boolean hasValidData() {
        return Objects.nonNull(sequence);
    }

    public MultiTrackMidiPlayer buildPlayer(int[] tracks) {
        return new MultiTrackMidiPlayer(
            Main.getKeyInput(),
            NoteConverter.convert(tracks, sequence),
            sequence.getMicrosecondLength() / sequence.getTickLength());
    }

}
