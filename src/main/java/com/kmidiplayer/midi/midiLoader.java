package com.kmidiplayer.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.kmidiplayer.App;
import com.kmidiplayer.gui.PrimaryController;
import com.kmidiplayer.keylogger.KeyboardInput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class midiLoader extends Thread {
    // final化出来るように
    private final KeyboardInput keyboardController;

    public midiLoader(KeyboardInput kinput) {
        keyboardController = kinput;
    }

    private static Sequence sequence;

    private static float tickInMilliSeconds;
    private static List<int[]> keyControlSequences = null;

    public static void loadFile(String midiDirectory) {
        try {
            System.out.println("trying load midi file...");
            sequence = MidiSystem.getSequence(new File(midiDirectory));
        } catch (IOException e) {
            System.out.println("File does not exist or does not have access rights.");
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            System.out.println("MIDI data is corrupted or incorrect.");
            e.printStackTrace();
        }
        if (sequence != null){
            System.out.println("midifile load is Done.");
        }

        // トラックごとにイベントを処理する
        List<MidiEvent> noteOnEvents = new ArrayList<>();
        List<MidiEvent> noteOffEvents = new ArrayList<>();
        for (Track track : sequence.getTracks()) {
            // トラック内のイベントを処理する
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if (sm.getCommand() == ShortMessage.NOTE_ON) {
                        int velocity = sm.getData2();
                        if (velocity != 0) {
                            // NOTE_ONをListに格納
                            noteOnEvents.add(event);
                        } else {
                            // NOTE_OFFをListに格納
                            noteOffEvents.add(event);
                        }
                    } else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
                        // NOTE_OFFをListに格納
                        noteOffEvents.add(event);
                    }
                }
            }
        }

        float tempoBPM = 120f; // デフォルト値
        
        // トラックの列挙
        for (Track track : sequence.getTracks()) {
            // MIDIイベントの列挙
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof MetaMessage) {
                    MetaMessage meta = (MetaMessage) message;
                    // テンポメッセージの場合
                    if (meta.getType() == 0x51) {
                        // テンポを算出
                        byte[] data = meta.getData();
                        int tempo = (data[0] & 0xFF) << 16 | (data[1] & 0xFF) << 8 | (data[2] & 0xFF);
                        tempoBPM = 60000000f / tempo;
                    }
                }
            }
        }

        // 1tickの秒数計算
        tickInMilliSeconds =  60f / (sequence.getResolution() * tempoBPM);

        System.out.println("1 tick = " + tickInMilliSeconds + " millisecond");

        // NOTE_ONとNOTE_OFF eventを時間順にソートし
        // すべてのイベントをtick順にまとめたリストとして再構築する
        List<MidiEvent> allEvents = new ArrayList<>();
        allEvents.addAll(noteOnEvents);
        allEvents.addAll(noteOffEvents);
        Collections.sort(allEvents, new Comparator<MidiEvent>() {
            public int compare(MidiEvent event1, MidiEvent event2) {
                return (int) (event1.getTick() - event2.getTick());
            }
        });

        // Listの内容を確認する(at debug)
        if(App.getKeyInput().isDebug()){
            for (MidiEvent event : allEvents) {
                System.out.println("NOTE_"+ ((ShortMessage) event.getMessage()).getData2() + "_" + ((ShortMessage) event.getMessage()).getData1() + " at tick :" + event.getTick());
            }
        }

        // 時間順にNOTE_ON,OFF,Thread.sleep(ms)をまとめたListを作成
        int prevTick =0;
        int diff =0;
        List<int[]> keyControlEventStuck = new ArrayList<>();
        // eventStuck int[ type, note, tick, diff, sequencesIndexNumber ]
        // type: on=1 off=1
        for (MidiEvent event : allEvents) {
            int type;
            int note = ((ShortMessage) event.getMessage()).getData1();
            int tick = (int) event.getTick();
            if (prevTick == (int) event.getTick()){
                diff = 0;
            } else {
                diff = (int) event.getTick() - prevTick;
            }
            prevTick = (int) event.getTick();

            if (event.getMessage() instanceof ShortMessage) {
                ShortMessage sm = (ShortMessage) event.getMessage();
                if (sm.getCommand() == ShortMessage.NOTE_ON) {
                    type = 0;
                } else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
                    type = 1;
                } else {
                    // ノート以外のイベントはスキップ
                    continue;
                }
            } else {
                // ノート以外のイベントはスキップ
                continue;
            }
            keyControlEventStuck.add(new int[]{type, note, tick, diff});
        }

        // 遅延を追加し新しいリストに格納する
        // 新しいリストではint[type, note/sleep]となる
        // type=0(on)/1(off)/2(sleep),sleep=ticks

        keyControlSequences = new ArrayList<>();
        for (int[] event : keyControlEventStuck){
            if (event[3] != 0){
                keyControlSequences.add(new int[]{2,event[3]});
                keyControlSequences.add(new int[]{event[0],event[1]});
            } else {
                keyControlSequences.add(new int[]{event[0],event[1]});
            }
        }
        PrimaryController.IsFileLoadSucsessSetter(true);
    }

    @Override
    public void run() {
        for (int[] event : keyControlSequences){
            switch (event [0]) {
                case 0: // NOTE_ON
                    keyboardController.KeyControl(event[1],true);
                    break;
                case 1: // NOTE_OFF
                    keyboardController.KeyControl(event[1],false);
                    break;
                case 2: // SLEEP
                    try {
                        if(keyboardController.isDebug()){
                            System.out.println("Try to Sleep "+(long)(event[1]*(tickInMilliSeconds*1000))+"MiliSeconds");
                        }
                        Thread.sleep((long)(event[1]*(tickInMilliSeconds*1000)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Invailed type Number!");
                    break;
            }
        }
        System.out.println("Output is Ended, result:");
        System.out.println("MaxOutOfRange:" + keyboardController.occurrencesOfOutOfRangeMax);
        System.out.println("Maximum Max difference :" + keyboardController.valeOfOutOfRangeMax);
        System.out.println("MinOutOfRange:" + keyboardController.occurrencesOfOutOfRangeMin);
        System.out.println("Maximum Min difference :" + keyboardController.valeOfOutOfRangeMin);
        // 次の再生に備えリセットする
        keyboardController.occurrencesOfOutOfRangeMax=0;
        keyboardController.occurrencesOfOutOfRangeMin=0;
        keyboardController.valeOfOutOfRangeMax = 0;
        keyboardController.valeOfOutOfRangeMin = 0;

    }
}
