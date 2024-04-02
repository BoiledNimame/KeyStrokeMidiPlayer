package com.kmidiplayer.midi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.App;
import com.kmidiplayer.gui.PrimaryController;

public class midiLoader implements midiCommandType {
    private static Logger logger = LogManager.getLogger("[Mid]");
    public static Sequence getSequencefromDirectory(File midiFile) {
        logger.info("trying load midi file...");
        try {
            try {
                final Sequence sequence = MidiSystem.getSequence(midiFile);
                if (sequence != null){
                    logger.info("midifile load is Done.");
                    PrimaryController.IsFileLoadSucsessSetter(true);
                }
                return sequence;
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

    public static List<MidiEvent> convertSequenceToMidiEvent(Sequence sequence) {
        if (sequence.getTracks().length != 1) {
            // TODO トラック毎の分割機能を作る
            logger.warn("Multiple tracks detected. Operation not guaranteed.");
        }

        // すべてのイベントをtick順にまとめたリスト
        final List<MidiEvent> allEvents = new ArrayList<>();

        for (Track track : sequence.getTracks()) {
            // トラック内のイベントを処理する
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                if (event.getMessage() instanceof ShortMessage) {
                    allEvents.add(event);
                }

            }
        }

        Collections.sort(allEvents, new Comparator<MidiEvent>() {
            public int compare(MidiEvent event1, MidiEvent event2) {
                return Math.toIntExact(event1.getTick() - event2.getTick());
            }
        });

        // Listの内容を確認する(at debug)
        if(App.getKeyInput().isDebug()){
            for (MidiEvent event : allEvents) {
                logger.debug("NOTE_"+ ((ShortMessage) event.getMessage()).getData2() + "_" + ((ShortMessage) event.getMessage()).getData1() + " at tick :" + event.getTick());
            }
        }
        return allEvents;
    }

    public static List<long[]> convertRawKeys(List<MidiEvent> rawEvents) {
        // eventStuck int[ type, note, tick, diff, sequencesIndexNumber ]
        final List<long[]> keyControlEventStuck = new ArrayList<>();
        CONVERT : for (int index = 0; index < rawEvents.size(); index++) {
            final MidiEvent event = rawEvents.get(index);
            //-------------------------type-------------------------//
            int type;
            if (event.getMessage() instanceof ShortMessage) {
                switch (((ShortMessage) event.getMessage()).getCommand()) {
                    case ShortMessage.NOTE_ON:
                        type = NOTE_ON;
                        break;

                    case ShortMessage.NOTE_OFF:
                        type = NOTE_OFF;
                        break;

                    default:
                        continue CONVERT;
                }
            } else {
                continue CONVERT;
            }
            //-------------------------note-------------------------//
            int note = ((ShortMessage) event.getMessage()).getData1();
            //-------------------------tick-------------------------//
            long tick = event.getTick();
            //-------------------------diff-------------------------//
            long diff;
            if (index != 0) {
                final MidiEvent prevEvent = rawEvents.get(index-1);
                if (prevEvent.getTick() == event.getTick()){
                    diff = 0;
                } else {
                    diff = event.getTick() - prevEvent.getTick();
                }
            } else {
                diff = event.getTick();
            }
            //------------------------return------------------------//
            keyControlEventStuck.add(new long[]{type, note, tick, diff});
        }
        return keyControlEventStuck;
    }
}
