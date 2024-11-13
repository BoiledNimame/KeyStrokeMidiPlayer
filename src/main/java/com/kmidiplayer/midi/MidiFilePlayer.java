package com.kmidiplayer.midi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import com.kmidiplayer.config.Options;
import com.kmidiplayer.keylogger.InputterSupplier;
import com.kmidiplayer.midi.data.HighPrecisionPlayerTask;
import com.kmidiplayer.midi.data.LowPrecisionPlayerTask;
import com.kmidiplayer.midi.event.INoteEventListener;
import com.kmidiplayer.midi.util.MidiFileChecker;
import com.kmidiplayer.midi.util.NoteConverter;
import com.kmidiplayer.midi.util.TrackInfo;

import io.github.palexdev.materialfx.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MidiFilePlayer {

    private static final Logger LOGGER = LogManager.getLogger("[MidiPlayer]");

    private final Sequence sequence;
    private final ScheduledExecutorService executor;

    private Future<?> task;
    private Runnable after;

    public MidiFilePlayer(File file) {
        if (MidiFileChecker.isValid(file)) {
            try {
                sequence = MidiSystem.getSequence(file);
                executor = Executors.newSingleThreadScheduledExecutor();
                listeners = new ArrayList<>();
            } catch (InvalidMidiDataException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("This file [" + file + "] is not a midi file.");
        }
    }

    public boolean isValid() {
        return sequence.getTracks().length!=0;
    }

    public boolean isAlive() {
        return Objects.nonNull(task) && !task.isDone();
    }

    public TrackInfo[] getTrackInfos() {
        return Stream.of(sequence.getTracks()).map(TrackInfo::new).toArray(TrackInfo[]::new);
    }

    public void play(int[] tracks, int initialDelay, int noteNumberOffset, String windowTitle, boolean useHighPrecision) {

        final int definedNoteMin = Options.configs.getKeyMap().keySet().stream().mapToInt(Integer::parseInt).min().orElse(-1);
        final int definedNoteMax = Options.configs.getKeyMap().keySet().stream().mapToInt(Integer::parseInt).max().orElse(-1);

        if (definedNoteMin == -1 || definedNoteMax == -1) {
            throw new RuntimeException("keymap.yaml is empty or could not be read successfully.");
        }

        final boolean isWindowTitleValid = Objects.nonNull(windowTitle) && !StringUtils.EMPTY.equals(windowTitle);

        if (useHighPrecision) {

            task = executor.scheduleAtFixedRate(
                new HighPrecisionPlayerTask(
                    InputterSupplier.getInstance(),
                    isWindowTitleValid ?  windowTitle : Options.configs.getWindowName(),
                    NoteConverter.convert(
                        tracks,
                        sequence,
                        definedNoteMin,
                        definedNoteMax,
                        noteNumberOffset),
                    this::stop,
                    listeners),
                initialDelay * 1000L, // Milliseconds --(*1000)-> Microseconds
                sequence.getMicrosecondLength() / sequence.getTickLength(), // getMicrosecondLength() -> full Length of Sequence as Microseconds, getTickLength() -> full Length of Sequence as Tick
                TimeUnit.MICROSECONDS
            );

        } else {

            // validate TickLength
            final long singleTickLength;
            final double singleTickLengthMillisecond = ((double) sequence.getMicrosecondLength() / sequence.getTickLength()) / 1000D;

            if (singleTickLengthMillisecond < 1D) {
                LOGGER.warn("singleTickLength(:{}) is too short! try to use High-Precision Mode", singleTickLengthMillisecond);
                singleTickLength = 1;
            } else {
                singleTickLength = Double.valueOf(Math.floor(((double)sequence.getMicrosecondLength() / sequence.getTickLength()) / 1000D)).longValue();
            }

            task = executor.scheduleAtFixedRate(
                new LowPrecisionPlayerTask(
                    InputterSupplier.getInstance(),
                    isWindowTitleValid ?  windowTitle : Options.configs.getWindowName(),
                    NoteConverter.convert(
                        tracks,
                        sequence,
                        definedNoteMin,
                        definedNoteMax,
                        noteNumberOffset),
                    sequence.getMicrosecondLength() / sequence.getTickLength(),
                    this::stop,
                    listeners),
                initialDelay,
                singleTickLength,
                TimeUnit.MILLISECONDS
            );

        }
    }

    public void playThen(int[] tracks, int initialDelay, int noteNumberOffset, String windowTitle, boolean useHighPrecision, Runnable before, Runnable after) {
        before.run();
        play(tracks, initialDelay, noteNumberOffset, windowTitle, useHighPrecision);
        this.after = after;
    }

    public void stop() {
        if (Objects.nonNull(executor)) {
            if (Objects.nonNull(task)) {
                task.cancel(true);
                task = null;
            }
        }
        if (after!=null) {
            after.run();
        }
    }

    private final List<INoteEventListener> listeners;

    public void addEventListener(INoteEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(INoteEventListener listener) {
        listeners.remove(listener);
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
