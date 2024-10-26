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
import javax.sound.midi.Track;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.midi.data.HighPrecisionPlayerTask;
import com.kmidiplayer.midi.data.LowPrecisionPlayerTask;
import com.kmidiplayer.midi.util.MidiFileChecker;
import com.kmidiplayer.midi.util.NoteConverter;

import io.github.palexdev.materialfx.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MidiFilePlayer {

    private static final Logger LOGGER = LogManager.getLogger("[MidiPlayer]");

    private final Sequence sequence;
    private ScheduledExecutorService executor;

    private Runnable after;

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

    public boolean isValid() {
        return Objects.nonNull(sequence) && sequence.getTracks().length!=0;
    }

    public boolean isAlive() {
        return Objects.nonNull(executor) && !executor.isShutdown();
    }

    public String[] getTrackInfos() {
        final Track[] tracks = sequence.getTracks();
        final String[] infos = new String[tracks.length];
        for (int i=0; i<tracks.length; i++) {
            infos[i] = (new StringBuilder()).append("Track: ")
                                            .append(i)
                                            .append(", Notes: ")
                                            .append(tracks[i].size())
                                            .toString();
        }
        return infos;
    }

    public void play(int[] tracks, int initialDelay, int noteNumberOffset, String windowTitle, boolean useHighPrecision) {

        if (!Objects.nonNull(sequence)) { return; }

        final boolean isWindowTitleValid = Objects.isNull(windowTitle) || StringUtils.EMPTY.equals(windowTitle);

        if (useHighPrecision) {
            executor.scheduleAtFixedRate(
                        new HighPrecisionPlayerTask(
                            Main.getKeyInput(),
                            isWindowTitleValid ? ConfigHolder.configs.getWindowName() : windowTitle,
                            NoteConverter.convert(tracks, sequence, noteNumberOffset),
                            this::stop),
                        initialDelay * 1000L, // Milliseconds --(*1000)-> Microseconds
                        sequence.getMicrosecondLength() / sequence.getTickLength(), // getMicrosecondLength() -> full Length of Sequence as Microseconds, getTickLength() -> full Length of Sequence as Tick
                        TimeUnit.MICROSECONDS);

        } else {

            long singleTickLength;
            final double singleTickLengthMillisecond = ((double) sequence.getMicrosecondLength() / sequence.getTickLength()) / 1000D;

            if (singleTickLengthMillisecond < 1D) {
                LOGGER.warn("singleTickLength(:{}) is too short! try to use High-Precision Mode", singleTickLengthMillisecond);
                singleTickLength = 1;
            } else {
                singleTickLength = Double.valueOf(Math.floor(((double)sequence.getMicrosecondLength() / sequence.getTickLength()) / 1000D)).longValue();
            }

            executor.scheduleAtFixedRate(
                        new LowPrecisionPlayerTask(
                            Main.getKeyInput(),
                            isWindowTitleValid ? ConfigHolder.configs.getWindowName() : windowTitle,
                            NoteConverter.convert(tracks, sequence, noteNumberOffset),
                            sequence.getMicrosecondLength() / sequence.getTickLength(),
                            this::stop),
                        initialDelay,
                        singleTickLength,
                        TimeUnit.MILLISECONDS);

        }
    }

    public void playThen(int[] tracks, int initialDelay, int noteNumberOffset, String windowTitle, boolean useHighPrecision, Runnable after) {
        play(tracks, initialDelay, noteNumberOffset, windowTitle, useHighPrecision);
        this.after = after;
    }

    public void stop() {
        if (Objects.nonNull(executor)) {
            executor.shutdownNow();
            executor = Executors.newSingleThreadScheduledExecutor();
        }
        if (after!=null) {
            after.run();
        }
    }
}
