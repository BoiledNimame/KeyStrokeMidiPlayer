package com.kmidiplayer.gui;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.util.Resource;

import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

// TODO MaterialFxによるViewを作る
public class MUIView {

    private final MUIController controller;
    private final Pane base;

    private final double HEIGHT = 156;
    public double getHeight() { return HEIGHT; };
    private final double WIDTH  = 214;
    public double getWidth() { return WIDTH; };

    private final String TITLE = "keystroke midifile player";
    public String getTitle() { return TITLE; };

    private final Image ICON;
    public Image getIcon() { return ICON; };

    public MUIView(Stage stage) {
        controller = new MUIController(this, stage);
        base = new AnchorPane();

        ICON = new Image(Resource.getFIleURLAsString(Main.class, "images", "icon.png"));
    }

    public Pane getBasePane() {
        return base;
    }

}
