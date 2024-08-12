package com.kmidiplayer.gui;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

// TODO MaterialFxによるViewを作る
public class MUIView {

    private final MUIController controller;
    private final Pane base;

    public MUIView() {
        controller = new MUIController(this);
        base = new AnchorPane();
    }

    public Pane getBasePane() {
        return base;
    }
}
