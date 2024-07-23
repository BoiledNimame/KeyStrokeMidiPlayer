package com.kmidiplayer.gui;

import java.util.Objects;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.application.UI;
import com.kmidiplayer.midi.integrated.MidiData;
import com.kmidiplayer.midi.integrated.MidiPlayer;
import com.kmidiplayer.midi.multi.MultiTrackMidiData;
import com.kmidiplayer.midi.multi.MultiTrackMidiPlayer;

// Model      : this
// View       : primary.fxml
// Controller : PrimaryController.java

// uiからのコントロールの受け+Drag&Dropにて与えられたmidiファイルデータの保持etc
public class PrimaryModel {

    // Singleton
    private PrimaryModel() {};
    private static final PrimaryModel instance = new PrimaryModel();
    static PrimaryModel getInstance() { return instance; };
    
    private boolean isFileLoaded;
    public void setFileLoaded(boolean bool) { isFileLoaded = bool; };
    public boolean isFileLoaded() { return isFileLoaded; };

    private MidiData midiData;
    private MidiPlayer player;
    private MultiTrackMidiData mMidiData;
    private MultiTrackMidiPlayer mPlayer;

    public boolean convertData() {
        if (mMidiData != null) {
            mPlayer = new MultiTrackMidiPlayer(Main.getKeyInput(), mMidiData.convert(), mMidiData.getTickMicroseconds());
            return Objects.isNull(mPlayer);
        }
        return true;
    }

    boolean startPlayer(boolean isDivine, int delaySeconds) {
        int sleepMillisecond = 10000;
        // 再生遅延
        try{
            int parsedSleepTime = delaySeconds*1000;
            sleepMillisecond = isDivine ? parsedSleepTime : parsedSleepTime<=10000 ? parsedSleepTime : 10000 ;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (isDivine) {
            try {
                Thread.sleep(sleepMillisecond);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
                // 別スレッドで再生開始
            if (midiData != null) {
                player = new MidiPlayer(Main.getKeyInput(), midiData, midiData.getTickInMilliSeconds());
                player.start();
                return true;
            } else {
                UI.logger().error("The midi file has not been converted correctly or is not working properly.");
                return false;
            }
        } else {
            if (mPlayer != null) {
                mPlayer.addAdvanceDelay(sleepMillisecond);
                mPlayer.start();
                return true;
            }
            return false;
        }
    }

    void clearPlayer() {
        if (player != null) {
            if (player.isAlive()) {
                player.interrupt();
                player = null;
                midiData = null;
            } else {
                player = null;
                midiData = null;
            }
        }
        if (mPlayer != null) {
            if (mPlayer.isAlive()) {
                mPlayer.interrupt();
                mPlayer = null;
                mMidiData = null;
            } else {
                mPlayer = null;
                mMidiData = null;
            }
        }
    }
}
