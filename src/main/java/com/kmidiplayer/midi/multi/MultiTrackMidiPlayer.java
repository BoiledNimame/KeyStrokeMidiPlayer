package com.kmidiplayer.midi.multi;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

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


    private static final int loops = 10; 
    private static long beginDateLong = 0;
    private static long loopsLong;
    private static long maxDelays;
    public static void main(String[] arg0) throws InterruptedException {
        // final Date date = new Date();
        // for (int i = 0; i < loops; i++) {
        //     final TestThread test = new TestThread(new Timer(), date, i);
        //     test.start();
        // }
        beginDateLong = (new Date()).getTime();
        final Timer timer = new Timer();
        final int period = 1;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long i = (new Date().getTime()) - beginDateLong;
                System.out.println((new StringBuilder())
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
                    System.out.println("maxErr: " + maxDelays);
                    System.exit(0);
                }
            }
        }, 0, period);
    }
    private static class TestThread extends Thread {
        final Timer timer;
        final Date date;
        final int loop;
        public TestThread(Timer timer, Date delayedDate, int loop) {
            this.timer = timer;
            this.date = delayedDate;
            this.loop = loop;
        }
        @Override
        public void run() {
            try {
                defferedExcecutioner(timer, date, loop, (loop+1)*1000);
            } catch (InterruptedException e) {
                // 潰し
            }
        }
    }
    private static void defferedExcecutioner(Timer timer, Date delayedDate, int loop, int delay) throws InterruptedException {
        final long begin = delayedDate.getTime();
        final CountDownLatch latch = new CountDownLatch(1);
        final TestTask task = new TestTask(latch);

        timer.schedule(task, delay);
        latch.await();
        logger.info("delays:" + delay + ", actually delay:" + (task.getExecuteTime()-begin) + ", miss:" + ((task.getExecuteTime()-begin)-delay));
        if (loop == (loops-1)) { System.exit(0); }
    }
    private static class TestTask extends TimerTask {
        long exec = 0;
        final CountDownLatch latch;
        public TestTask(CountDownLatch latch) {
            this.latch = latch;
        }
        @Override
        public void run() {
            exec = new Date().getTime();
            latch.countDown();
        }
        public long getExecuteTime() {
            return exec;
        }
    }
}
