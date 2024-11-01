package com.kmidiplayer.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.kmidiplayer.config.Cache;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.midi.util.MidiFileChecker;
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

    private final List<Runnable> terminations;

    MUIController(MUIView view, Stage stage) {
        model = new MUIModel(view);

        Cache.init();
        stage.showingProperty().addListener(this::termination);
        terminations = new ArrayList<>();

        terminations.add(model::stop);
        terminations.add(model::shutdown);
        terminations.add(() -> Cache.toCache(model.getPathFieldItem()));
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
            Objects.requireNonNull(db.getFiles());
            final List<File> dropped_Files = db.getFiles();
            if (!dropped_Files.isEmpty()) {
                dropped_Files.stream()
                    .filter(MidiFileChecker::isValid)
                    .findFirst()
                    .ifPresent(a -> model.setPath(a.getAbsolutePath()));
                dropped_Files.stream()
                    .filter(MidiFileChecker::isValid)
                    .forEach(a -> model.addItemIfNotContains(a.getAbsolutePath()));
            }
        }
        event.setDropCompleted(HAS_DB_FILES);
        event.consume();
    }

    void pathTextListener(ObservableValue<? extends String> v, String o, String n) {
        updatePlayers();
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

    ObservableList<String> getCacheData() {
        return Cache.getCache(); // ただのラッパー
    }

    private void termination(ObservableValue<? extends Boolean> o, Boolean a, Boolean b) {
        // ウィンドウが閉じた直後に行われる終了処理
        if (a && !b) {
            terminations.forEach(Runnable::run);
        }
    }
}
