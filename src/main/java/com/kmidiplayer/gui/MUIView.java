package com.kmidiplayer.gui;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.util.Resource;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

// TODO MaterialFxによるViewを作る
public class MUIView {

    private final MUIController controller;
    private final BorderPane BASE;

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
        ICON = new Image(Resource.getFIleURLAsString(Main.class, "images", "icon.png"));
        BASE = new BorderPane();

            final VBox VBOX = new VBox();

                final Button jButton = new Button("Normal");
                jButton.setMaxWidth(Double.MAX_VALUE);

                final MFXButton mbutton = new MFXButton("MaterialFx");
                mbutton.setMaxWidth(Double.MAX_VALUE);

            VBOX.setSpacing(10);
            VBOX.getChildren().addAll(mbutton, jButton);

        BASE.setPadding(new Insets(20, 20, 20, 20));
        BASE.setCenter(VBOX);
    }

    public Pane getBasePane() {
        return BASE;
    }

}
