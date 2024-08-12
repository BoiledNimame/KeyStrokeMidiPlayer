package com.kmidiplayer.gui;

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

    private void termination() {
        // ウィンドウが閉じた直後に行われる終了処理
    }
}
