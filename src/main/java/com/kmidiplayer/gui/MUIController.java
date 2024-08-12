package com.kmidiplayer.gui;

public class MUIController {
    private final MUIModel model;

    MUIController(MUIView view) {
        model = new MUIModel(view);
    }
}
