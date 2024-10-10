package com.kmidiplayer.midi;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.midi.data.HighPrecisionPlayerTask;
import com.kmidiplayer.midi.data.LowPrecisionPlayerTask;
import com.kmidiplayer.midi.util.MidiFileChecker;
import com.kmidiplayer.midi.util.NoteConverter;

import io.github.palexdev.materialfx.utils.StringUtils;

public class MidiFilePlayer {

    private final Sequence sequence;
    private ScheduledExecutorService executor;

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
        }
    }

    public void play(int[] tracks, int initialDelay, String windowTitle) {
        if (!Objects.nonNull(sequence)) { return; }
        if (ConfigHolder.configs.useHighPrecisionMode()) {
            executor.scheduleAtFixedRate(
                        new HighPrecisionPlayerTask(
                            Main.getKeyInput(),
                            Objects.isNull(windowTitle) || StringUtils.EMPTY.equals(windowTitle) ? ConfigHolder.configs.getWindowName() : windowTitle,
                            NoteConverter.convert(tracks, sequence),
                            this::stop),
                        initialDelay,
                        sequence.getTickLength(),
                        TimeUnit.MICROSECONDS);
        } else {
            executor.scheduleAtFixedRate(
                        new LowPrecisionPlayerTask(
                            Main.getKeyInput(),
                            Objects.isNull(windowTitle) || StringUtils.EMPTY.equals(windowTitle) ? ConfigHolder.configs.getWindowName() : windowTitle,
                            NoteConverter.convert(tracks, sequence),
                            sequence.getMicrosecondLength() / sequence.getTickLength(),
                            this::stop),
                        initialDelay,
                        (sequence.getMicrosecondLength() / sequence.getTickLength()) / 1000,
                        TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        executor.shutdownNow();
        executor = Executors.newSingleThreadScheduledExecutor();
    }
}
