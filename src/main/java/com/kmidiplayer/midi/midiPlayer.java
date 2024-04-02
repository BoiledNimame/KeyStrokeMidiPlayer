package com.kmidiplayer.midi;

import java.util.List;

import com.kmidiplayer.gui.Gui;
import com.kmidiplayer.keylogger.KeyboardInput;

public class midiPlayer extends Thread {
    private final KeyboardInput kInput;
    private final List<long[]> keyArr;
    public final double tickInMilliSeconds;
    
    public midiPlayer(KeyboardInput inputter, midiData data, double tickInMilliSeconds) {
        kInput = inputter;
        keyArr = data.getplayableKeyArr();
        this.tickInMilliSeconds = tickInMilliSeconds;
    }

    private final static int NOTE_ON = 0;
    private final static int NOTE_OFF = 1;
    private final static int SLEEP = 2;

    @Override
    public void run() {
        for (long[] event : keyArr){
            // TODO 正確に実行されているかはかなり怪しい チョー怪しい...(コード自体はコピペで分離しただけなので変更点なし, scheduleへ移行を検討)
            // MEMO scheduleへ移行する :: sequenceのgetTickLength()がmicrosecで帰るから, Date->begin + ((getTickLength()/1000)*tick)でscheduleする(scheduleはミリ秒)
            if (event[0] == NOTE_ON) {
                kInput.KeyControl(Math.toIntExact(event[1]) ,true);
            } else if (event[0] == NOTE_OFF) {
                kInput.KeyControl(Math.toIntExact(event[1]),false);
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
        Gui.logger().info("MaxOutOfRange:" + kInput.occurrencesOfOutOfRangeMax);
        Gui.logger().info("Maximum Over_difference :" + kInput.valeOfOutOfRangeMax);
        Gui.logger().info("MinOutOfRange:" + kInput.occurrencesOfOutOfRangeMin);
        Gui.logger().info("Maximum Less_difference :" + kInput.valeOfOutOfRangeMin);

        // 次の再生に備えリセットする
        kInput.occurrencesOfOutOfRangeMax=0;
        kInput.occurrencesOfOutOfRangeMin=0;
        kInput.valeOfOutOfRangeMax = 0;
        kInput.valeOfOutOfRangeMin = 0;
    }
}
