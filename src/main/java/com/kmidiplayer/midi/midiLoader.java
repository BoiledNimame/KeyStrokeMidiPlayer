package com.kmidiplayer.midi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.kmidiplayer.App;
import com.kmidiplayer.gui.Gui;
import com.kmidiplayer.gui.PrimaryController;

public class midiLoader {
    public static Sequence getSequencefromDirectory(String midiDirectory) {
        Gui.logger().info("trying load midi file...");
        try {
            final File file = new File(midiDirectory);
            try {
                final Sequence sequence = MidiSystem.getSequence(file);
                if (sequence != null){
                    Gui.logger().info("midifile load is Done.");
                }
                return sequence;
            } catch (InvalidMidiDataException e) {
                Gui.logger().info("MIDI data is corrupted or incorrect.");
                e.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            Gui.logger().info("File does not exist or does not have access rights.");
            e.printStackTrace();
            return null;
        }
    }

    public static List<MidiEvent> convertSequenceToMidiEvent(Sequence sequence) {
        if (sequence.getTracks().length != 1) {
            App.logger().warn("Multiple tracks detected. Operation not guaranteed.");
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

        double tempoBPM = 120D; // デフォルト値
        
        GET_TEMPO : {
            for (Track track : sequence.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();
                    if (message instanceof MetaMessage) {
                        MetaMessage meta = (MetaMessage) message;
    
                        final int TEMPO_MESSAGE = 0x51;
                        if (meta.getType() == TEMPO_MESSAGE) {
                            // テンポを算出
                            byte[] data = meta.getData();
                            int tempo = (data[0] & 0xFF) << 16 | (data[1] & 0xFF) << 8 | (data[2] & 0xFF);
                            tempoBPM = 60000000D / tempo;
                            break GET_TEMPO;
                        }
                    }
                }
            }
        }

        // 1tickの秒数計算
        final double tickInMilliSeconds =  60D / (sequence.getResolution() * tempoBPM);

        Gui.logger().info("1 tick = " + tickInMilliSeconds + " millisecond");

        // Listの内容を確認する(at debug)
        if(App.getKeyInput().isDebug()){
            for (MidiEvent event : allEvents) {
                Gui.logger().debug("NOTE_"+ ((ShortMessage) event.getMessage()).getData2() + "_" + ((ShortMessage) event.getMessage()).getData1() + " at tick :" + event.getTick());
            }
        }
        return allEvents;
    }

    static final int NOTE_ON = 0;
    static final int NOTE_OFF = 1;
    static final int SLEEP = 2;
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

        // 遅延を追加し新しいリストに格納する
        // 新しいリストではlong[type, note/sleep]となる
        final List<long[]> keyControlSequences = new ArrayList<>();
        for (long[] event : keyControlEventStuck){
            // ひとつ前のコマンドからの経過時間
            if (event[3] != 0){
                keyControlSequences.add(new long[]{SLEEP, event[3]});
                keyControlSequences.add(new long[]{event[0], event[1]});
            } else {
                keyControlSequences.add(new long[]{event[0], event[1]});
            }
        }
        // これ自体はFx Thread以外から呼ばれることを意図していない
        PrimaryController.IsFileLoadSucsessSetter(true);
        return keyControlSequences;
    }
}
