package com.kmidiplayer.gui;

import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.midi.MidiFilePlayer;

import javafx.scene.Node;

public class MUIModel {

    private final static Logger LOGGER = LogManager.getLogger("MUI-Model");

    private final MUIView view;

    MUIModel(MUIView view) {
        this.view = view;
    }

    void setPath(String text) {
        if ("".equals(text) || text==null) {
            view.getPathField().clear();
        } else {
            view.getPathField().setText(text);
        }
    }

    private MidiFilePlayer player;

    boolean isPlayerValid() {
        return player!=null && player.isValid();
    }

    void generatePlayer() {
        if (!"".equals(getFieldPath()) || player!=null && !player.isAlive()) {
            player = new MidiFilePlayer(Paths.get(getFieldPath()).toFile());
            LOGGER.info(player.isValid() ? "File Loaded successfully." : "File cannot be read or is corrupt.");
        }
    }

    String[] getTrackInfos() {
        return player.getTrackInfos();
    }

    void play(int[] tracks) {
        if (player!=null && player.isValid()) {
            player.play(
                tracks,
                "".equals(view.getInputDelayField().getText()) ? 0 : Integer.valueOf(view.getInputDelayField().getText()),
                view.getWindowNameField().getText(),
                view.getUseHighPrecisionCheckBox().selectedProperty().get()
            );
        }
    }

    void stop() {
        if (player!=null && player.isAlive()) {
            player.stop();
        }
    }

    void addToSelectorHolderAllAndRefresh(Node[] selectors) {
        view.getTrackSelectorHolder().getChildren().addAll(selectors);
        view.getTrackSelectorHolder().setPrefHeight(selectors.length!=0 ? selectors[0].getScaleY()*selectors.length : 0);
    }

    void clearSelectedHolder() {
        if (!view.getTrackSelectorHolder().getChildren().isEmpty()) {
            view.getTrackSelectorHolder().getChildren().clear();
        }
    }

    private String getFieldPath() {
        return view.getPathField().getText();
    }

    public void setPlayButtonDisable(boolean b) {
        view.getPlayButton().setDisable(b);
    }

    public void setStopButtonDisable(boolean b) {
        view.getStopButton().setDisable(b);
    }

}
