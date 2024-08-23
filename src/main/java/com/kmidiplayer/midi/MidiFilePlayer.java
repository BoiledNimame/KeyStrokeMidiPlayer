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

    private boolean hasValidData() {
        return Objects.nonNull(sequence);
    }

    public void play(int[] tracks, int initialDelay, String windowTitle) {
        if (!hasValidData()) { return; }
        future = executor.scheduleAtFixedRate(
                    new PlayerTask(
                        Main.getKeyInput(),
                        this,
                        Objects.isNull(windowTitle) || StringUtils.EMPTY.equals(windowTitle) ? ConfigHolder.instance().getWindowName() : windowTitle,
                        NoteConverter.convert(tracks, sequence),
                        initialDelay),
                    initialDelay,
                    (sequence.getMicrosecondLength() / sequence.getTickLength()) / 1000,
                    TimeUnit.MILLISECONDS);
    }

    public void cancel() {
        if (future != null) {
            future.cancel(true);
        }
    }

}
