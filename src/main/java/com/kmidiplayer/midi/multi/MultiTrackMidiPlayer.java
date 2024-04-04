package com.kmidiplayer.midi.multi;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.keylogger.KeyboardInput;

public class MultiTrackMidiPlayer extends Thread {
    private static final Logger logger = LogManager.getLogger("[Mid]");

    private final KeyboardInput kInput;
    private final List<KeyCommand> keyInputComponent;
    private final long tickMicroseconds;
    private int advancedDelayMilliseconds;

    public MultiTrackMidiPlayer(KeyboardInput inputter, List<KeyCommand> keys, long microsecondsOf1tick) {
        kInput = inputter;
        keyInputComponent = keys;
        tickMicroseconds = microsecondsOf1tick;
    }

    public void addAdvanceDelay(int Milliseconds) {
        advancedDelayMilliseconds = Milliseconds;
    }

    @Override
    public void run() {
        final Logger logger = LogManager.getLogger("MidiPlayer");
        // 精度はミリ秒で十分そう -> Timer.schedule()で実装
        logger.info(keyInputComponent.size());
        // for (KeyCommand cmd : keyInputComponent) {
        //    logger.debug("isPress:" + (cmd.isPush ? cmd.isPush + " " : cmd.isPush) + ", note:" + cmd.note + ", tick:" + cmd.tick + ", millis:" + ((cmd.tick * tickMicroseconds) / 1000));
        // }
    }


    //------------------------------------------以下テストコード------------------------------------------//


    private static long beginDateLong = 0;
    private static long loopsLong;
    private static long maxDelays;
    public static void main(String[] arg0) throws InterruptedException {
        beginDateLong = (new Date()).getTime();
        final Timer timer = new Timer();
        final int period = 1;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long i = (new Date().getTime()) - beginDateLong;
                logger.info((new StringBuilder())
                    .append(loopsLong)
                    .append(":")
                    .append(i)
                    .append(", err:")
                    .append(i-(loopsLong*period))
                    .toString()
                );
                loopsLong++;
                maxDelays = maxDelays < (i-(loopsLong*period)) ? i-(loopsLong*period) : maxDelays;
                if (loopsLong > 100) {
                    logger.info("maxErr: " + maxDelays);
                    System.exit(0);
                }
            }
        }, 0, period);
    }
}
