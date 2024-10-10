package com.kmidiplayer.midi.integrated;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.kmidiplayer.application.UI;
import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.config.ConfigHolder.Configs;
import com.kmidiplayer.keylogger.IInputter;
import com.kmidiplayer.keylogger.VkCodeMap;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class MidiPlayer extends Thread implements midiCommandType {
    private final IInputter kInput;
    private final List<long[]> keyArr;
    public final double tickInMilliSeconds;

    private final boolean isDebug;
    private final Map<String, String> config;
    private final int noteNumberOffset;
    private final boolean isCopyNearestNote;
    private final boolean forceUsingVKCode;
    private final int noteRangeMin;
    private final int noteRangeMax;
    private final String windowName;

    public MidiPlayer(IInputter inputter, MidiData data, double tickInMilliSeconds) {
        Configs holder = ConfigHolder.configs;
        isDebug = holder.isDebug();
        kInput = inputter;
        keyArr = data.getplayableKeyArr();
        this.tickInMilliSeconds = tickInMilliSeconds;
        config = holder.getKeyMap();
        noteNumberOffset = holder.getNoteOffset();
        isCopyNearestNote = holder.isCopyNearestNote();
        forceUsingVKCode = holder.isUsingVkCode();
        noteRangeMin = holder.getMinNote();
        noteRangeMax = holder.getMaxNote();
        windowName = holder.getWindowName();
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
            // MEMO scheduleへ移行する :: sequenceのgetTickLength()がmicrosecで帰るから, Date->begin + ((getTickLength()/1000)*tick)でscheduleする(scheduleはミリ秒)
            if (event[0] == NOTE_ON) {
                KeyControl(Math.toIntExact(event[1]) ,true);
            } else if (event[0] == NOTE_OFF) {
                KeyControl(Math.toIntExact(event[1]),false);
            } else if (event[0] == SLEEP) {
                if(isDebug){
                    UI.logger().debug("Try to Sleep "+(long)(event[1]*(this.tickInMilliSeconds*1000))+"MiliSeconds");
                }
                try {
                    Thread.sleep((long)(event[1]*(this.tickInMilliSeconds*1000)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                UI.logger().error("Invailed type Number!");
            }
        }

        UI.logger().info("Output is Ended, result:");
        UI.logger().info("MaxOutOfRange:" + occurrencesOfOutOfRangeMax);
        UI.logger().info("Maximum Over_difference :" + valeOfOutOfRangeMax);
        UI.logger().info("MinOutOfRange:" + occurrencesOfOutOfRangeMin);
        UI.logger().info("Maximum Less_difference :" + valeOfOutOfRangeMin);

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
                    vkCode = VkCodeMap.GetVKcode(config.get(Integer.toString(buffedNoteNumber)));
                } else {
                    vkCode = Integer.parseInt(config.get(Integer.toString(buffedNoteNumber)));
                }
            }
        } else {
            if(!forceUsingVKCode){
                vkCode = VkCodeMap.GetVKcode(config.get(Integer.toString(buffedNoteNumber)));
            } else {
                vkCode = Integer.parseInt(config.get(Integer.toString(buffedNoteNumber)));
            }
        }

        User32 user32 = User32.INSTANCE;
        WinDef.HWND hWnd = user32.FindWindow(null, windowName);

        kInput.keyInput(user32, hWnd, itPush, vkCode);
    }
}
