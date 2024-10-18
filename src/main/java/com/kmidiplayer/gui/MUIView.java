package com.kmidiplayer.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.util.Resource;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// TODO MaterialFxによるViewを作る
public class MUIView {

    private static final Logger LOGGER = LogManager.getLogger("[UIV]");

    private final MUIController controller;
    private final AnchorPane BASE;

    private final double HEIGHT = 384.0D;
    public double getHeight() { return HEIGHT; };
    private final double WIDTH  = 600.0D;
    public double getWidth() { return WIDTH; };

    private final String TITLE = "keystroke midifile player";
    public String getTitle() { return TITLE; };

    private final Image ICON;
    public Image getIcon() { return ICON; };

    public MUIView(Stage stage) {
        controller = new MUIController(this, stage);
        ICON = new Image(Resource.getFIleURLAsString(Main.class, "images", "icon.png"));
        BASE = new AnchorPane();
        BASE.setStyle("-fx-background-color: white");
            final Text dropText1 = new Text("↑");
             dropText1.setLayoutX(169.0D);
             dropText1.setLayoutY(106.0D);
            final Text dropText2 = new Text("Drag & Drop here");
             dropText2.setLayoutX(127.0D);
             dropText2.setLayoutY(123.0D);
            final AnchorPane fileDropArea = new AnchorPane(dropText1, dropText2);
             fileDropArea.setPrefHeight(220.0D);
             fileDropArea.setPrefWidth(350.0D);
             fileDropArea.setLayoutX(14.0D);
             fileDropArea.setLayoutY(42.0D);
             fileDropArea.setStyle("-fx-background-color: #D3D3D3;");
            final TextField midPathField = new TextField();
             midPathField.setLayoutX(14.0D);
             midPathField.setLayoutY(17.0D);
             midPathField.setPromptText("path");
             midPathField.setPrefHeight(25.0D);
             midPathField.setPrefWidth(285.0D);
            final Button pathReset = new Button();
             pathReset.setLayoutX(300.0D);
             pathReset.setLayoutY(17.0D);
             pathReset.setPrefHeight(25.0D);
             pathReset.setPrefWidth(65.0D);
             pathReset.setText("reset");
            final Button playButton = new Button();
             playButton.setLayoutX(13.0D);
             playButton.setLayoutY(344.0D);
             playButton.setPrefHeight(20.0D);
             playButton.setPrefWidth(175.0D);
             playButton.setText("Play");
            final Button stopButton = new Button();
             stopButton.setLayoutX(189.0D);
             stopButton.setLayoutY(344.0D);
             stopButton.setPrefHeight(20.0D);
             stopButton.setPrefWidth(175.0D);
             stopButton.setText("Stop");
            final TextField inputDelay = new TextField();
             inputDelay.setLayoutX(189.0D);
             inputDelay.setLayoutY(310.0D);
             inputDelay.setPrefHeight(25.0D);
             inputDelay.setPrefWidth(175.0D);
             inputDelay.setPromptText("delay (sec)");
            final TextField windowName = new TextField(ConfigHolder.configs.getWindowName());
             windowName.setLayoutX(189.0D);
             windowName.setLayoutY(274.0D);
             windowName.setPrefHeight(25.0D);
             windowName.setPrefWidth(175.0D);
             windowName.setPromptText("window name");
            final CheckBox useHighPrecision = new CheckBox();
             useHighPrecision.setLayoutX(15.0D);
             useHighPrecision.setLayoutY(314.0D);
             useHighPrecision.setText("use high-precision mode");
            final VBox trackSelectoHolderPane = new VBox();
             trackSelectoHolderPane.setPrefHeight(400.0D);
             trackSelectoHolderPane.setPrefWidth(185.0D);
            final ScrollPane trackSelectorHolderWrapperPane = new ScrollPane(trackSelectoHolderPane);
             trackSelectorHolderWrapperPane.setPrefHeight(329.0D);
             trackSelectorHolderWrapperPane.setPrefWidth(200.0D);
             AnchorPane.setRightAnchor(trackSelectorHolderWrapperPane, 15.0D);
             AnchorPane.setBottomAnchor(trackSelectorHolderWrapperPane, 15.0D);
            final Label trackSelectorLabel = new Label();
             trackSelectorLabel.setText("tracks");
             AnchorPane.setRightAnchor(trackSelectorLabel, 180.0D);
             AnchorPane.setTopAnchor(trackSelectorLabel, 20.0D);
        BASE.getChildren().addAll(fileDropArea, midPathField, pathReset, playButton, stopButton, inputDelay, windowName, useHighPrecision, trackSelectorLabel, trackSelectorHolderWrapperPane);
    }

    public Pane getBasePane() {
        return BASE;
    }

}
