package com.kmidiplayer.midi.integrated;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.kmidiplayer.gui.Gui;
import com.kmidiplayer.keylogger.KeyboardInput;
import com.kmidiplayer.keylogger.KeycordMap;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class MidiPlayer extends Thread implements midiCommandType {
    private final KeyboardInput kInput;
    private final List<long[]> keyArr;
    public final double tickInMilliSeconds;

    private final Map<String, String> config;
    private final int noteNumberOffset;
    private final boolean isCopyNearestNote;
    private final boolean forceUsingVKCode;
    private final int noteRangeMin;
    private final int noteRangeMax;
    private final String windowName;
    
    public MidiPlayer(KeyboardInput inputter, MidiData data, double tickInMilliSeconds) {
        kInput = inputter;
        keyArr = data.getplayableKeyArr();
        this.tickInMilliSeconds = tickInMilliSeconds;
        config = kInput.config();
        noteNumberOffset = kInput.getNoteNumberOffset();
        isCopyNearestNote = kInput.isCopyNearestNote();
        forceUsingVKCode = kInput.isForceUsingVKCode();
        noteRangeMin = kInput.getNoteLimit()[0];
        noteRangeMax = kInput.getNoteLimit()[1];
        windowName = kInput.getWindowName();
    }

    @Override
    public void run() {

        // 遅延を追加し新しいリストに格納する
        // 新しいリストではlong[type, note/sleep]となる
        final List<long[]> keyControlSequences = new ArrayList<>();
        for (long[] event : keyArr){
            // ひとつ前のコマンドからの経過時間
            if (event[3] != 0){
                keyControlSequences.add(new long[]{SLEEP, event[3]});
                keyControlSequences.add(new long[]{event[0], event[1]});
            } else {
                keyControlSequences.add(new long[]{event[0], event[1]});
            }
        }

        // keyControlEventStuck.add(new long[]{type, note, tick, diff}

        for (long[] event : keyArr){
            // TODO 正確に実行されているかはかなり怪しい チョー怪しい...(コード自体はコピペで分離しただけなので変更点なし, scheduleへ移行を検討)
            // MEMO scheduleへ移行する :: sequenceのgetTickLength()がmicrosecで帰るから, Date->begin + ((getTickLength()/1000)*tick)でscheduleする(scheduleはミリ秒)
            if (event[0] == NOTE_ON) {
                KeyControl(Math.toIntExact(event[1]) ,true);
            } else if (event[0] == NOTE_OFF) {
                KeyControl(Math.toIntExact(event[1]),false);
            } else if (event[0] == SLEEP) {
                if(kInput.isDebug()){
                    Gui.logger().debug("Try to Sleep "+(long)(event[1]*(this.tickInMilliSeconds*1000))+"MiliSeconds");
                }
                try {
                    Thread.sleep((long)(event[1]*(this.tickInMilliSeconds*1000)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Gui.logger().error("Invailed type Number!");
            }
        }

        Gui.logger().info("Output is Ended, result:");
        Gui.logger().info("MaxOutOfRange:" + occurrencesOfOutOfRangeMax);
        Gui.logger().info("Maximum Over_difference :" + valeOfOutOfRangeMax);
        Gui.logger().info("MinOutOfRange:" + occurrencesOfOutOfRangeMin);
        Gui.logger().info("Maximum Less_difference :" + valeOfOutOfRangeMin);

        // 次の再生に備えリセットする
        occurrencesOfOutOfRangeMax=0;
        occurrencesOfOutOfRangeMin=0;
        valeOfOutOfRangeMax = 0;
        valeOfOutOfRangeMin = 0;
    }

    public int occurrencesOfOutOfRangeMax = 0;
    public int valeOfOutOfRangeMax = 0;
    public int occurrencesOfOutOfRangeMin = 0;
    public int valeOfOutOfRangeMin = 0;

    public void KeyControl(int noteNumber, boolean itPush){
        int vkCode = 0;
        int buffedNoteNumber = noteNumber + noteNumberOffset;

        if (isCopyNearestNote){
            if (buffedNoteNumber > noteRangeMax){
                occurrencesOfOutOfRangeMax++;
                if((noteRangeMax-buffedNoteNumber) < valeOfOutOfRangeMax){
                    valeOfOutOfRangeMax = (noteRangeMax-buffedNoteNumber);
                }
                vkCode = noteRangeMax;
            } else if (noteRangeMin > buffedNoteNumber){
                occurrencesOfOutOfRangeMin++;
                if(valeOfOutOfRangeMin < (noteRangeMin-buffedNoteNumber)){
                    valeOfOutOfRangeMin = (noteRangeMin-buffedNoteNumber);
                }
                vkCode = noteRangeMin;
            } else {
                if(!forceUsingVKCode){
                    vkCode = KeycordMap.GetVKcode(config.get(Integer.toString(buffedNoteNumber)));
                } else {
                    vkCode = Integer.parseInt(config.get(Integer.toString(buffedNoteNumber)));
                }
            }
        } else {
            if(!forceUsingVKCode){
                vkCode = KeycordMap.GetVKcode(config.get(Integer.toString(buffedNoteNumber)));
            } else {
                vkCode = Integer.parseInt(config.get(Integer.toString(buffedNoteNumber)));
            }
        }

        User32 user32 = User32.INSTANCE;
        WinDef.HWND hWnd = user32.FindWindow(null, windowName);
    
        kInput.keyInput(user32, hWnd, itPush, vkCode);
    }
}
