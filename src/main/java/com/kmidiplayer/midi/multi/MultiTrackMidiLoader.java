package com.kmidiplayer.midi.multi;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.gui.PrimaryController;

public class MultiTrackMidiLoader {
    private static Logger logger = LogManager.getLogger("[Mid]");

    public static MultiTrackMidiData loadFileToDataObject(File file) {
        logger.info("trying load midi file...");
        try {
            try {
                final Sequence sequence = MidiSystem.getSequence(file);
                if (sequence != null){
                    logger.info("midifile load is Done.");
                    PrimaryController.IsFileLoadSucsessSetter(true);
                }
                return new MultiTrackMidiData(sequence);
            } catch (InvalidMidiDataException e) {
                logger.info("MIDI data is corrupted or incorrect.");
                e.printStackTrace();
                PrimaryController.IsFileLoadSucsessSetter(false);
                return null;
            }
        } catch (IOException e) {
            logger.info("File does not exist or does not have access rights.");
            e.printStackTrace();
            PrimaryController.IsFileLoadSucsessSetter(false);
            return null;
        }
    }

    public static String[] getTrackInfoFromSequence(Sequence sequence) {
        final String[] result = new String[sequence.getTracks().length];
        for (int i = 0; i < sequence.getTracks().length; i++) {
            result[i] = (i+", cmd: " + sequence.getTracks()[i].size());
        }
        return result;
    }

    public static List<KeyCommand> convert(int trackIndex, Sequence sequence) {
        final List<KeyCommand> result = new ArrayList<>();
        final Track processingTrack = sequence.getTracks()[trackIndex];
        CONVERT : for (int index = 0; index < processingTrack.size(); index++) {
            if (processingTrack.get(index).getMessage() instanceof ShortMessage) {
                final MidiEvent event = processingTrack.get(index);
                switch (((ShortMessage) event.getMessage()).getCommand()) {
                    case ShortMessage.NOTE_ON:
                        result.add(new KeyCommand(
                            true,
                            processingTrack.get(index).getTick(),
                            ((ShortMessage) processingTrack.get(index).getMessage()).getData1()));
                        break;
                    case ShortMessage.NOTE_OFF:
                        result.add(new KeyCommand(
                            false,
                            processingTrack.get(index).getTick(),
                            ((ShortMessage) processingTrack.get(index).getMessage()).getData1()));
                        break;
                
                    default:
                        continue CONVERT;
                }
            }
        }
        return result;
    }
}
