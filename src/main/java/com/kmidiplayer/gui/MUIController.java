package com.kmidiplayer.gui;

import javafx.event.ActionEvent;
import javafx.scene.input.DragEvent;
import javafx.stage.Stage;

public class MUIController {
    private final Stage stage;
    private final MUIModel model;

    MUIController(MUIView view, Stage stage) {
        this.stage = stage;
        model = new MUIModel(view);

        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) { termination(); }
        });
    }

    void fileDropArea_dragOver(DragEvent event) {

    }

    void fileDropArea_Entered(DragEvent event) {

    }

    void fileDropArea_Existed(DragEvent event) {

    }

    void fileDropArea_dragDropped(DragEvent event) {

    }

    void pathReset_onAction(ActionEvent event) {

    }

    void playButton_onAction(ActionEvent event) {

    }

    void stopButton_onAction(ActionEvent event) {

    }

    private void termination() {
        // ウィンドウが閉じた直後に行われる終了処理
    }
}
