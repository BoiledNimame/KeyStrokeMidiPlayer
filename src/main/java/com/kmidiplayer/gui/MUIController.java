package com.kmidiplayer.gui;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.midi.util.TrackInfo;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

public class MUIController {

    private final static Logger LOGGER = LogManager.getLogger("[MUI-Controller]");

    private final MUIModel model;

    MUIController(MUIView view, Stage stage) {
        model = new MUIModel(view);

        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) { termination(); }
        });
    }

    void fileDropArea_dragOver(DragEvent event) {
            if (event.getGestureSource() != event.getSource() &&
                    event.getDragboard().hasFiles()){
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
    }

    void fileDropArea_dragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        final boolean HAS_DB_FILES = db.hasFiles();
        if (HAS_DB_FILES){
            List<File> dropped_Files = db.getFiles();
            Objects.requireNonNull(dropped_Files.get(0));
            LOGGER.info("Loaded File Path: {}", dropped_Files.get(0).toString());
            model.setPath(dropped_Files.get(0).getAbsolutePath());

            updatePlayers();
        }
        event.setDropCompleted(HAS_DB_FILES);
        event.consume();
    }

    private void updatePlayers() {
        model.generatePlayer();
        model.clearSelectedHolder();
        if (model.isPlayerValid()) {
            model.addToSelectorHolderAllAndRefresh(generateTrackSelectToggleButton(model.getTrackInfo()));
        } else {
            model.setPlayButtonDisable(true);
        }
    }

    private Node[] generateTrackSelectToggleButton(TrackInfo[] infos) {
        final MFXToggleButton[] selectors = new MFXToggleButton[infos.length];
        final int maxLengthOfNoteCount = Stream.of(infos).mapToInt(m -> String.valueOf(m.getNotes()).length()).max().getAsInt();
        for(int i=0; i<infos.length; i++) {
            selectors[i] = new MFXToggleButton();
            selectors[i].setText(
                "Notes: "
                .concat(" ".repeat(maxLengthOfNoteCount - String.valueOf(infos[i].getNotes()).length()))
                .concat(String.valueOf(infos[i].getNotes()))
                .concat(", ")
                .concat(TrackInfo.getInstrumentFromProgramChange(infos[i].getProgramChange())));
            selectors[i].setId(String.valueOf(i));
            selectors[i].setOnAction(this::generatedToggleOnAction);
        }
        return selectors;
    }

    private void generatedToggleOnAction(ActionEvent event) {
        model.setPlayButtonEnableWhenToggleButtonEnabled();
        event.consume();
    }

    void pathReset_onAction(ActionEvent event) {
        model.clearSelectedHolder();
        model.setPath("");
    }

    void playButton_onAction(ActionEvent event) {
        model.play();
        model.setPlayButtonDisable(true);
        model.setStopButtonDisable(false);
    }

    void stopButton_onAction(ActionEvent event) {
        LOGGER.info("task is cancelled!");
        model.stop();
        model.setPlayButtonDisable(false);
        model.setStopButtonDisable(true);
    }

    private void termination() {
        // ウィンドウが閉じた直後に行われる終了処理
        model.stop();
        model.shutdown();
    }
}
