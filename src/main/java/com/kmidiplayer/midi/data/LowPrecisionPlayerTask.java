package com.kmidiplayer.midi.data;

import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.keylogger.IInputter;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class LowPrecisionPlayerTask implements Runnable {

    private final static Logger LOGGER = LogManager.getLogger("L.Player");

    private final Runnable stopper;

    private final IInputter inputter;
    private final KeyCommand[][] iCommand;

    private int counter;
    private final int maxCount;

    private final User32 user32 = User32.INSTANCE;
    private final WinDef.HWND hWnd;

    public LowPrecisionPlayerTask(IInputter inputter, String windowTitle, KeyCommand[] inputComponent, long singleTickLengthMicroseconds, Runnable stopper) {

        this.stopper = stopper;

        this.inputter = inputter;
        final KeyCommand[] commands = inputComponent;
        final int internalTick = calcInternalTick(singleTickLengthMicroseconds);

        counter = 0;

        if (commands == null) {
            throw new IllegalArgumentException("inputCompornent is null!");
        } else if (commands.length == 0) {
            throw new IllegalArgumentException("inputCompornent's length is 0!");
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
                if (Arrays.stream(commands)
                    .filter(cmd -> cmd.tick <= internalTick*(internalTickIndex)).count() != 0) {
                        this.iCommand[i] = Arrays.stream(commands)
                        .filter(cmd -> cmd.tick <= internalTick*(internalTickIndex))
                        .toArray(KeyCommand[]::new);
                } else {
                    this.iCommand[i] = EMPTY_KEYS_ARRAY;
                }
            } else {
                if (Arrays.stream(commands)
                    .filter(cmd -> cmd.tick <= internalTick*(internalTickIndex) && cmd.tick > internalTick*(internalTickIndex-1)).count() != 0) {
                        this.iCommand[i] = Arrays.stream(commands)
                        .filter(cmd -> cmd.tick <= internalTick*(internalTickIndex) && cmd.tick > internalTick*(internalTickIndex-1))
                        .toArray(KeyCommand[]::new);
                } else {
                    this.iCommand[i] = EMPTY_KEYS_ARRAY;
                }
            }
        }

        // カウンターの限界を置いとく
        maxCount = this.iCommand.length;
    }

    @Override
    public void run() {
        if (maxCount <= counter) {
            System.out.println(this.iCommand[counter]);
            LOGGER.info("Sequence completed, stop execution of this task.");
            if (stopper != null) {
                stopper.run();
            }
        }
        if (this.iCommand[counter].length != 0) {
            for (KeyCommand key : iCommand[counter]) {
                inputter.keyInput(user32, hWnd, key.isPush, key.vkCode);
            }
        }
        counter++;
    }

    private static int calcInternalTick(long singleTickLengthMicroseconds) {
        int internalTick = 1;
        BigDecimal millisOfSingleTick = (new BigDecimal(singleTickLengthMicroseconds)).divide(new BigDecimal(1000));
        BigDecimal remainder = new BigDecimal(singleTickLengthMicroseconds / 1000D);
        for (int i =1; i <= 10; i++) {
            BigDecimal r = millisOfSingleTick.multiply(new BigDecimal(i)).subtract(new BigDecimal(Math.floor((millisOfSingleTick.multiply(new BigDecimal(i))).doubleValue())));
            String log = "l:" + i + ", tick: " + r;
            if (r.compareTo(remainder) < 0) {
                log.concat(": updateThisValue");
                remainder = millisOfSingleTick.multiply(new BigDecimal(i)).subtract(new BigDecimal(Math.floor((millisOfSingleTick.multiply(new BigDecimal(i))).doubleValue())));
                internalTick = i;
            }
            if (ConfigHolder.configs.isDebug()) { LOGGER.debug("l:" + i + ", tick: " + r); }
        }
        remainder = millisOfSingleTick.multiply(new BigDecimal(internalTick));
        if (ConfigHolder.configs.isDebug()) { LOGGER.debug("internalTick: " + internalTick + ", internalTickTimeMillisec:" + remainder.doubleValue()); }
        return remainder.intValue();
    }
}
