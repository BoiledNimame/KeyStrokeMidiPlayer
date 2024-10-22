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
    private final AnchorPane ROOT;

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

    public MUIView(Stage stage, String defaultStyle) {

        controller = new MUIController(this, stage);

        DEFAULT_STYLE = defaultStyle;
        CUSTOM_STYLE = MUIView.class.getResource("View.css").toExternalForm();

        ICON = new Image(Resource.getFIleURLAsString(Main.class, "images", "icon.png"));

        ROOT = new AnchorPane();
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
              fileDropArea.setOnDragEntered(controller::fileDropArea_Entered);
              fileDropArea.setOnDragExited(controller::fileDropArea_Existed);
              fileDropArea.setOnDragDropped(controller::fileDropArea_dragDropped);
            final MFXTextField midPathField = new MFXTextField();
             midPathField.setId("TextField_MPath");
             midPathField.setLayoutX(14.0D);
             midPathField.setLayoutY(17.0D);
             midPathField.setPrefHeight(25.0D);
             midPathField.setPrefWidth(285.0D);
             midPathField.setFloatingText("path");
             midPathField.setFloatMode(FloatMode.BORDER);
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
            final MFXTextField inputDelay = new MFXTextField();
            inputDelay.setId("TextField_Input");
             inputDelay.setLayoutX(13.0);
             inputDelay.setLayoutY(290.0D);
             inputDelay.setPrefHeight(25.0D);
             inputDelay.setPrefWidth(175.0D);
             inputDelay.setFloatingText("delay (sec)");
             inputDelay.setFloatMode(FloatMode.BORDER);
            final MFXTextField windowName = new MFXTextField(ConfigHolder.configs.getWindowName());
            inputDelay.setId("TextField_WName");
             windowName.setLayoutX(13.0);
             windowName.setLayoutY(244.0D);
             windowName.setPrefHeight(25.0D);
             windowName.setPrefWidth(175.0D);
             windowName.setPromptText("window name");
             windowName.setFloatingText("window name");
             windowName.setFloatMode(FloatMode.BORDER);
            final MFXCheckbox useHighPrecision = new MFXCheckbox();
             useHighPrecision.setId("CheckBox_useHP");
             useHighPrecision.setLayoutX(189.0D);
             useHighPrecision.setLayoutY(295.0D);
             useHighPrecision.setText("use high-precision mode");
            final VBox trackSelectoHolderPane = new VBox();
             trackSelectoHolderPane.setId("VBox_TrackHolder");
             trackSelectoHolderPane.setPrefHeight(400.0D);
             trackSelectoHolderPane.setPrefWidth(185.0D);
            final MFXScrollPane trackSelectorHolderWrapperPane = new MFXScrollPane(trackSelectoHolderPane);
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
        ROOT.getChildren().addAll(fileDropArea, midPathField, pathReset, playButton, stopButton, inputDelay, windowName, useHighPrecision, trackSelectorLabel, trackSelectorHolderWrapperPane);

        addStyleSheetAll(CUSTOM_STYLE, ROOT);
        addStyleSheetAll(DEFAULT_STYLE, ROOT);
    }

    public Pane getRootPane() {
        return ROOT;
    }

    private static void addStyleSheetAll(String style, Pane pane) {
        pane.getStylesheets().add(style);
        Arrays.stream(pane.getChildren().toArray(Parent[]::new))
              .forEach(n -> n.getStylesheets().add(style));
    }

}
