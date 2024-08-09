package com.kmidiplayer.midi.multi;

import java.math.BigDecimal;

import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.keylogger.IInputter;

public class MultiTrackMidiPlayer extends Thread {
    private static final Logger logger = LogManager.getLogger("[Mid]");

    private final IInputter kInput;
    private final KeyCommand[] keyInputComponent;
    private final long tickMicroseconds;
    private int advancedDelayMilliseconds;

    public MultiTrackMidiPlayer(IInputter inputter, KeyCommand[] keys, long microsecondsOf1tick) {
        kInput = inputter;
        keyInputComponent = keys;
        tickMicroseconds = microsecondsOf1tick;
    }

    public void addAdvanceDelay(int Milliseconds) {
        advancedDelayMilliseconds = Milliseconds;
    }

    @Override
    public void run() {
        // 最終的な実行時間のズレが無くなるようにしたい.
        final int internalTick = getInternalTick(tickMicroseconds);

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TaskController(kInput, timer, keyInputComponent, internalTick), advancedDelayMilliseconds, (tickMicroseconds / 1000));
    }

    private int getInternalTick(long microsecondsOfSingleTick) {
        int internalTick = 1;
        BigDecimal millisOfSingleTick = (new BigDecimal(microsecondsOfSingleTick)).divide(new BigDecimal(1000));
        BigDecimal remainder = new BigDecimal(microsecondsOfSingleTick / 1000D);
        for (int i =1; i <= 10; i++) {
            BigDecimal r = millisOfSingleTick.multiply(new BigDecimal(i)).subtract(new BigDecimal(Math.floor((millisOfSingleTick.multiply(new BigDecimal(i))).doubleValue())));
            String log = "l:" + i + ", tick: " + r;
            if (r.compareTo(remainder) < 0) {
                log.concat(": updateThisValue");
                remainder = millisOfSingleTick.multiply(new BigDecimal(i)).subtract(new BigDecimal(Math.floor((millisOfSingleTick.multiply(new BigDecimal(i))).doubleValue())));
                internalTick = i;
            }
            if (ConfigHolder.instance().isDebug()) { logger.debug("l:" + i + ", tick: " + r); }
        }
        remainder = millisOfSingleTick.multiply(new BigDecimal(internalTick));
        if (ConfigHolder.instance().isDebug()) { logger.debug("internalTick: " + internalTick + ", internalTickTimeMillisec:" + remainder.doubleValue()); }
        return remainder.intValue();
    }
}
