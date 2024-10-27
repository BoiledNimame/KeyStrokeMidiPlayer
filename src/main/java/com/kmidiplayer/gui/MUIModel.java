package com.kmidiplayer.gui;

import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.midi.MidiFilePlayer;
import com.kmidiplayer.midi.util.TrackInfo;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.scene.Node;
import javafx.scene.control.Toggle;

public class MUIModel {

    private final static Logger LOGGER = LogManager.getLogger("[MUI-Model]");

    private final static String EMPTY = "";

    private final MUIView view;

    MUIModel(MUIView view) {
        this.view = view;
    }

    void setPath(String text) {
        if (EMPTY.equals(text) || text==null) {
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

    TrackInfo[] getTrackInfo() {
        return player.getTrackInfos();
    }

    void play() {
        if (player!=null && player.isValid()) {
            player.playThen(
                view.getTrackSelectorHolder().getChildren().stream()
                    .filter(p -> p instanceof MFXToggleButton)
                    .map(m -> (MFXToggleButton) m)
                    .filter(p -> p.selectedProperty().get())
                    .mapToInt(m -> Integer.parseInt(m.getId()))
                    .toArray(),
                EMPTY.equals(view.getInputDelayField().getText()) ? 0 : Integer.parseInt(view.getInputDelayField().getText()),
                Integer.parseInt(view.getNOTE_OFFSET().getText()),
                view.getWindowNameField().getText(),
                view.getUseHighPrecisionCheckBox().selectedProperty().get(),
                this::after
            );
        }
    }

    void stop() {
        if (player!=null && player.isAlive()) {
            player.stop();
        }
    }

    void after() {
        // 再生終了時の処理
        // トラックが選択されていればplayを有効化(stopは必ず無効に)
        view.getPlayButton().setDisable(
            view.getTrackSelectorHolder().getChildren().stream()
                .filter(p -> p instanceof Toggle)
                .map(m -> (Toggle) m)
                .noneMatch(p -> p.selectedProperty().get()));
        view.getStopButton().setDisable(true);
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

    void setPlayButtonEnableWhenToggleButtonEnabled() {
        if (!view.getTrackSelectorHolder().getChildren().isEmpty()) {
            setPlayButtonDisable(
                view.getTrackSelectorHolder().getChildren().stream()
                    .filter(p -> p instanceof MFXToggleButton)
                    .map(m -> (MFXToggleButton) m)
                    .noneMatch(p -> p.selectedProperty().get()));
        }
    }

    void setPlayButtonDisable(boolean b) {
        view.getPlayButton().setDisable(b);
    }

    void setStopButtonDisable(boolean b) {
        view.getStopButton().setDisable(b);
    }

}
