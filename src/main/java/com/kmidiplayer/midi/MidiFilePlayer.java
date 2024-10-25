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

public class MidiFilePlayer {

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

    public void play(int[] tracks, int initialDelay, String windowTitle, boolean useHighPrecision) {
        if (!Objects.nonNull(sequence)) { return; }
        final boolean isWindowTitleValid = Objects.isNull(windowTitle) || StringUtils.EMPTY.equals(windowTitle);
        if (useHighPrecision) {
            executor.scheduleAtFixedRate(
                        new HighPrecisionPlayerTask(
                            Main.getKeyInput(),
                            isWindowTitleValid ? ConfigHolder.configs.getWindowName() : windowTitle,
                            NoteConverter.convert(tracks, sequence),
                            this::stop),
                        initialDelay,
                        sequence.getTickLength(),
                        TimeUnit.MICROSECONDS);
        } else {
            executor.scheduleAtFixedRate(
                        new LowPrecisionPlayerTask(
                            Main.getKeyInput(),
                            isWindowTitleValid ? ConfigHolder.configs.getWindowName() : windowTitle,
                            NoteConverter.convert(tracks, sequence),
                            sequence.getMicrosecondLength() / sequence.getTickLength(),
                            this::stop),
                        initialDelay,
                        (sequence.getMicrosecondLength() / sequence.getTickLength()) / 1000, // ここちょっとダメかも(milliseconds要求してるのに 0.8ぐらいになっちゃうせいで上手く行ってない)
                        TimeUnit.MILLISECONDS);
        }
    }

    public void playThen(int[] tracks, int initialDelay, String windowTitle, boolean useHighPrecision, Runnable after) {
        play(tracks, initialDelay, windowTitle, useHighPrecision);
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
