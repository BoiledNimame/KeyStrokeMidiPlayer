package com.kmidiplayer.gui;

import java.io.File;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

public class MUIController {

    private final static Logger LOGGER = LogManager.getLogger("[MUI-Model]");

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
            LOGGER.info( "Loaded File Path: \"" + dropped_Files.get(0).toString() + "\"" );
            model.setPath(dropped_Files.get(0).getAbsolutePath());
        }
        event.setDropCompleted(HAS_DB_FILES);
        event.consume();
    }

    void pathReset_onAction(ActionEvent event) {
        model.setPath("");
    }

    void playButton_onAction(ActionEvent event) {

    }

    void stopButton_onAction(ActionEvent event) {

    }

    private void termination() {
        // ウィンドウが閉じた直後に行われる終了処理
    }
}
