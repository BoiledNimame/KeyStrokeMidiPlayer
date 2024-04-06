package com.kmidiplayer.midi.multi;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.gui.PrimaryController;
import com.kmidiplayer.keylogger.KeycordMap;

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

    public static KeyCommand[] convert(int trackIndex, Sequence sequence) {
        final Track processingTrack = sequence.getTracks()[trackIndex];
        final KeyCommand[] result = new KeyCommand[processingTrack.size()];

        // FIXME:config調整用のログ出力 65行でバグってら
        int OverRangedNotes = 0;
        int LessRangedNotes = 0;
        CORRECTINFO : for (int index = 0; index < processingTrack.size(); index++) {
            if (processingTrack.get(0).getMessage() instanceof ShortMessage) {
                final int MessageType = ((ShortMessage) processingTrack.get(index).getMessage()).getCommand();
                if (MessageType==ShortMessage.NOTE_ON || MessageType==ShortMessage.NOTE_OFF)
                if (config.getMaxNote() < ((ShortMessage) processingTrack.get(index).getMessage()).getData1()) {
                    OverRangedNotes++;
                } else if (((ShortMessage) processingTrack.get(index).getMessage()).getData1() < config.getMinNote()) {
                    LessRangedNotes++;
                }
            } else {
                continue CORRECTINFO;
            }
        }
        logger.info("Less Notes:" + LessRangedNotes + ", Over Notes:" + OverRangedNotes);
        logger.info("If this number is too large, review the config.json.");

        CONVERT : for (int index = 0; index < processingTrack.size(); index++) {
            if (processingTrack.get(index).getMessage() instanceof ShortMessage) {
                final MidiEvent event = processingTrack.get(index);
                switch (((ShortMessage) event.getMessage()).getCommand()) {
                    case ShortMessage.NOTE_ON:
                        result[index] = (new KeyCommand(
                            true,
                            processingTrack.get(index).getTick(),
                            convertNoteToVkCode(((ShortMessage) processingTrack.get(index).getMessage()).getData1())));
                        break;
                    case ShortMessage.NOTE_OFF:
                        result[index] = (new KeyCommand(
                            false,
                            processingTrack.get(index).getTick(),
                            convertNoteToVkCode(((ShortMessage) processingTrack.get(index).getMessage()).getData1())));
                        break;
                
                    default:
                        continue CONVERT;
                }
            }
        }
        return Arrays.stream(result).filter(item -> item!=null).toArray(KeyCommand[]::new);
    }

    private static final ConfigHolder config = ConfigHolder.instance();
    private static int convertNoteToVkCode(int noteNumber) {
        int vkCode = 0;
        final int buffedNoteNumber = noteNumber + config.getNoteOffset();
        if (config.isCopyNearestNote()){
            if (buffedNoteNumber > config.getMaxNote()){
                vkCode = config.getMaxNote();
            } else if (config.getMinNote() > buffedNoteNumber){
                vkCode = config.getMinNote();
            } else {
                if(!config.isUsingVkCode()){
                    vkCode = KeycordMap.GetVKcode(config.getKeyMap().get(Integer.toString(buffedNoteNumber)));
                } else {
                    vkCode = Integer.parseInt(config.getKeyMap().get(Integer.toString(buffedNoteNumber)));
                }
            }
        } else {
            if(!config.isUsingVkCode()){
                vkCode = KeycordMap.GetVKcode(config.getKeyMap().get(Integer.toString(buffedNoteNumber)));
            } else {
                vkCode = Integer.parseInt(config.getKeyMap().get(Integer.toString(buffedNoteNumber)));
            }
        }
        return vkCode;
    }
}
