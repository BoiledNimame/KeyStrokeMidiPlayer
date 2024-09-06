package com.kmidiplayer.midi;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.midi.data.PlayerTask;
import com.kmidiplayer.midi.util.MidiFileChecker;
import com.kmidiplayer.midi.util.NoteConverter;

import io.github.palexdev.materialfx.utils.StringUtils;

public class MidiFilePlayer {

    private final Sequence sequence;
    private final ScheduledExecutorService executor;
    private Future<?> future;

    public MidiFilePlayer(File file) {
        if (MidiFileChecker.isValid(file)) {
            try {
                sequence = MidiSystem.getSequence(file);
                executor = Executors.newSingleThreadScheduledExecutor();
            } catch (InvalidMidiDataException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            sequence = null;
            executor = null;
        }
    }

    public void play(int[] tracks, int initialDelay, String windowTitle) {
        if (!Objects.nonNull(sequence)) { return; }
        if (ConfigHolder.instance().useHighPrecisionMode()) {
            /*
             * scheduleAtFixedRate は TimeUnit.MICROSECONDS を受け付けるので "高精度実行" のオプションを用意する
             * 結局突っ込んだらいいのはRunnableなので, 新しいTask用classを作って入れることにする
             * 配列は確かソート済みなんで...
             * 実行間隔 sequence.getTickLength(); で取る
             * いっそのことPlayerTaskは消してもいいかも?
             */
        } else {
            future = executor.scheduleAtFixedRate(
                        new PlayerTask(
                            Main.getKeyInput(),
                            this,
                            Objects.isNull(windowTitle) || StringUtils.EMPTY.equals(windowTitle) ? ConfigHolder.instance().getWindowName() : windowTitle,
                            NoteConverter.convert(tracks, sequence),
                            sequence.getMicrosecondLength() / sequence.getTickLength()),
                        initialDelay,
                        (sequence.getMicrosecondLength() / sequence.getTickLength()) / 1000,
                        TimeUnit.MILLISECONDS);
        }
    }

    public void cancel() {
        if (future != null) {
            future.cancel(true);
        }
    }

}
