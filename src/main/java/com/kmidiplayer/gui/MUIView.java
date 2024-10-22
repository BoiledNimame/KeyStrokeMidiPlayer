package com.kmidiplayer.gui;

import java.util.Arrays;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.util.Resource;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MUIView {

    private final MUIController controller;

    private final double HEIGHT = 384.0D;
    public double getHeight() { return HEIGHT; };
    private final double WIDTH  = 600.0D;
    public double getWidth() { return WIDTH; };

    private final String TITLE = "keystroke midifile player";
    public String getTitle() { return TITLE; };

    private final Image ICON;
    public Image getIcon() { return ICON; };

    private final String DEFAULT_STYLE;
    private final String CUSTOM_STYLE;

    private final AnchorPane ROOT;
    private final MFXTextField PATHFIELD;
    private final VBox TRACK_HOLDER;
    private final MFXTextField WINDOW_NAME;
    private final MFXTextField INPUT_DELAY;
    private final MFXCheckbox USE_HIGH_PRECISION;

    public MUIView(Stage stage, String defaultStyle) {

        controller = new MUIController(this, stage);

        DEFAULT_STYLE = defaultStyle;
        CUSTOM_STYLE = MUIView.class.getResource("View.css").toExternalForm();

        ICON = new Image(Resource.getFIleURLAsString(Main.class, "images", "icon.png"));

        ROOT = new AnchorPane();
        ROOT.setId("Root");
            final Text dropText1 = new Text("↑");
             dropText1.setId("Text_Drop");
             dropText1.setLayoutX(169.0D);
             dropText1.setLayoutY(91.0D);
            final Text dropText2 = new Text("Drag & Drop here");
             dropText2.setId("Text_Drop");
             dropText2.setLayoutX(127.0D);
             dropText2.setLayoutY(108.0D);
            final AnchorPane fileDropArea = new AnchorPane(dropText1, dropText2);
             fileDropArea.setId("AnchorPane_DropArea");
             fileDropArea.setPrefHeight(180.0D);
             fileDropArea.setPrefWidth(350.0D);
             fileDropArea.setLayoutX(14.0D);
             fileDropArea.setLayoutY(42.0D);
              fileDropArea.setOnDragOver(controller::fileDropArea_dragOver);
              fileDropArea.setOnDragDropped(controller::fileDropArea_dragDropped);
            PATHFIELD = new MFXTextField();
             PATHFIELD.setId("TextField_MPath");
             PATHFIELD.setLayoutX(14.0D);
             PATHFIELD.setLayoutY(17.0D);
             PATHFIELD.setPrefHeight(25.0D);
             PATHFIELD.setPrefWidth(285.0D);
             PATHFIELD.setFloatingText("path");
             PATHFIELD.setFloatMode(FloatMode.BORDER);
            final MFXButton pathReset = new MFXButton();
            pathReset.setId("Button_Reset");
             pathReset.setLayoutX(300.0D);
             pathReset.setLayoutY(17.0D);
             pathReset.setPrefHeight(37.5D);
             pathReset.setPrefWidth(65.0D);
             pathReset.setText("reset");
             pathReset.setButtonType(ButtonType.FLAT);
              pathReset.setOnAction(controller::pathReset_onAction);
            final MFXButton playButton = new MFXButton();
            playButton.setId("Button_Play");
             playButton.setLayoutX(13.0D);
             playButton.setLayoutY(339.0D);
             playButton.setPrefHeight(30.0D);
             playButton.setPrefWidth(175.0D);
             playButton.setText("Play");
             playButton.setButtonType(ButtonType.FLAT);
              playButton.setOnAction(controller::playButton_onAction);
            final MFXButton stopButton = new MFXButton();
            stopButton.setId("Button_Stop");
             stopButton.setLayoutX(189.0D);
             stopButton.setLayoutY(339.0D);
             stopButton.setPrefHeight(30.0D);
             stopButton.setPrefWidth(175.0D);
             stopButton.setText("Stop");
             stopButton.setButtonType(ButtonType.FLAT);
              stopButton.setOnAction(controller::stopButton_onAction);
            INPUT_DELAY = new MFXTextField();
            INPUT_DELAY.setId("TextField_Input");
             INPUT_DELAY.setLayoutX(13.0);
             INPUT_DELAY.setLayoutY(290.0D);
             INPUT_DELAY.setPrefHeight(25.0D);
             INPUT_DELAY.setPrefWidth(175.0D);
             INPUT_DELAY.setFloatingText("delay (sec)");
             INPUT_DELAY.setFloatMode(FloatMode.BORDER);
            WINDOW_NAME = new MFXTextField(ConfigHolder.configs.getWindowName());
            WINDOW_NAME.setId("TextField_WName");
             WINDOW_NAME.setLayoutX(13.0);
             WINDOW_NAME.setLayoutY(244.0D);
             WINDOW_NAME.setPrefHeight(25.0D);
             WINDOW_NAME.setPrefWidth(175.0D);
             WINDOW_NAME.setPromptText("window name");
             WINDOW_NAME.setFloatingText("window name");
             WINDOW_NAME.setFloatMode(FloatMode.BORDER);
            USE_HIGH_PRECISION = new MFXCheckbox();
             USE_HIGH_PRECISION.setId("CheckBox_useHP");
             USE_HIGH_PRECISION.setLayoutX(189.0D);
             USE_HIGH_PRECISION.setLayoutY(295.0D);
             USE_HIGH_PRECISION.setText("use high-precision mode");
            TRACK_HOLDER = new VBox();
             TRACK_HOLDER.setId("VBox_TrackHolder");
             TRACK_HOLDER.setPrefHeight(0.0D);
             TRACK_HOLDER.setPrefWidth(185.0D);
            final MFXScrollPane trackSelectorHolderWrapperPane = new MFXScrollPane(TRACK_HOLDER);
             trackSelectorHolderWrapperPane.setId("ScrollPane_HolderWrapper");
             trackSelectorHolderWrapperPane.setPrefHeight(329.0D);
             trackSelectorHolderWrapperPane.setPrefWidth(200.0D);
             AnchorPane.setRightAnchor(trackSelectorHolderWrapperPane, 15.0D);
             AnchorPane.setBottomAnchor(trackSelectorHolderWrapperPane, 15.0D);
            final Label trackSelectorLabel = new Label();
             trackSelectorLabel.setId("Text_TSelector");
             trackSelectorLabel.setText("tracks");
             AnchorPane.setRightAnchor(trackSelectorLabel, 180.0D);
             AnchorPane.setTopAnchor(trackSelectorLabel, 20.0D);
        ROOT.getChildren().addAll(fileDropArea, PATHFIELD, pathReset, playButton, stopButton, INPUT_DELAY, WINDOW_NAME, USE_HIGH_PRECISION, trackSelectorLabel, trackSelectorHolderWrapperPane);

        addStyleSheetAll(CUSTOM_STYLE, ROOT);
        addStyleSheetAll(DEFAULT_STYLE, ROOT);
    }

    public Pane getRootPane() {
        return ROOT;
    }

    MFXTextField getPathField() {
        return PATHFIELD;
    }

    VBox getTrackSelectorHolder() {
        return TRACK_HOLDER;
    }

    MFXTextField getWindowNameField() {
        return WINDOW_NAME;
    }

    MFXTextField getInputDelayField() {
        return INPUT_DELAY;
    }

    MFXCheckbox getUseHighPrecisionCheckBox() {
        return USE_HIGH_PRECISION;
    }

    private static void addStyleSheetAll(String style, Pane pane) {
        pane.getStylesheets().add(style);
        Arrays.stream(pane.getChildren().toArray(Parent[]::new))
              .forEach(n -> n.getStylesheets().add(style));
    }

}
