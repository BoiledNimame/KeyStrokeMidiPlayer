package com.kmidiplayer.midi.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.Options;
import com.kmidiplayer.keylogger.IInputter;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class LowPrecisionPlayerTask implements Runnable {

    private final static Logger LOGGER = LogManager.getLogger("L.Player");

    private final Runnable stopper;

    private final IInputter inputter;
    private final KeyCommand[][] iCommand;

    private int currentIndex;
    private final int maxIndex;

    private final User32 user32 = User32.INSTANCE;
    private final WinDef.HWND hWnd;

    public LowPrecisionPlayerTask(IInputter inputter, String windowTitle, KeyCommand[] inputComponent, long singleTickLengthMicroseconds, Runnable stopper) {

        this.stopper = stopper;

        this.inputter = inputter;
        final KeyCommand[] commands = inputComponent;
        final int internalTick = calcInternalTick(singleTickLengthMicroseconds);

        currentIndex = 0;

        if (commands == null) {
            throw new IllegalArgumentException("inputComponent is null!");
        } else if (commands.length == 0) {
            throw new IllegalArgumentException("inputComponent's length is 0!");
        }

        hWnd = user32.FindWindow(null, windowTitle);

        // 実際に再生するデータは2次元配列となる
        iCommand = new KeyCommand[(Math.toIntExact(commands[commands.length-1].tick)/internalTick)+1][];

        // null参照にならないよう挿入する空配列
        final KeyCommand[] EMPTY_KEYS_ARRAY = new KeyCommand[0];

        // 渡された入力データから再生データを構築する. 何もしないtickは空配列とする
        for (int i = 0; i < (commands[commands.length-1].tick/internalTick)+1; i++) {
            final int internalTickIndex = i;
            // 一番最初だけは扱いが違う
            if (i==0) {
                if (Arrays.stream(commands).anyMatch(cmd -> cmd.tick <= 0)) {
                        this.iCommand[i] = Arrays.stream(commands)
                        .filter(cmd -> cmd.tick <= 0)
                        .toArray(KeyCommand[]::new);
                } else {
                    this.iCommand[i] = EMPTY_KEYS_ARRAY;
                }
            } else {
                if (Arrays.stream(commands).anyMatch(cmd -> cmd.tick <= (long) internalTick * (internalTickIndex) && cmd.tick > (long) internalTick * (internalTickIndex - 1))) {
                        this.iCommand[i] = Arrays.stream(commands)
                        .filter(cmd -> cmd.tick <= (long) internalTick *(internalTickIndex) && cmd.tick > (long) internalTick *(internalTickIndex-1))
                        .toArray(KeyCommand[]::new);
                } else {
                    this.iCommand[i] = EMPTY_KEYS_ARRAY;
                }
            }
        }

        // カウンターの限界を置いとく
        maxIndex = this.iCommand.length;
    }

    @Override
    public void run() {
        if (maxIndex <= currentIndex) {
            LOGGER.info("Sequence completed, stop execution of this task.");
            if (stopper != null) {
                stopper.run();
            }
        }
        for (KeyCommand key : iCommand[currentIndex]) {
            inputter.keyInput(user32, hWnd, key.isPush, key.vkCode);
        }
        currentIndex++;
    }

    private static int calcInternalTick(long singleTickLengthMicroseconds) {
        int internalTick = 1;
        BigDecimal millisOfSingleTick = (new BigDecimal(singleTickLengthMicroseconds)).divide(new BigDecimal(1000), RoundingMode.HALF_UP);
        BigDecimal remainder = new BigDecimal(singleTickLengthMicroseconds / 1000D);
        for (int i =1; i <= 10; i++) {
            BigDecimal r = millisOfSingleTick.multiply(new BigDecimal(i)).subtract(BigDecimal.valueOf(Math.floor((millisOfSingleTick.multiply(new BigDecimal(i))).doubleValue())));
            String log = "l:" + i + ", tick: " + r;
            if (r.compareTo(remainder) < 0) {
                log = log.concat(": useThisValue");
                remainder = r;
                internalTick = i;
            }
            if (Options.configs.isDebug()) { LOGGER.debug(log); }
        }
        remainder = millisOfSingleTick.multiply(new BigDecimal(internalTick));
        if (Options.configs.isDebug()) { LOGGER.debug("internalTick: " + internalTick + ", internalTickTimeMillisec:" + remainder.doubleValue()); }
        return remainder.intValue();
    }
}
