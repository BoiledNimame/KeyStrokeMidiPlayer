package com.kmidiplayer.gui;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

// TODO MaterialFxによるViewを作る
public class MUIView {

    private static final Logger LOGGER = LogManager.getLogger("[UIV]");

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

    public MUIView(Stage stage) {
        controller = new MUIController(this, stage);
        ICON = new Image(Resource.getFIleURLAsString(Main.class, "images", "icon.png"));
        ROOT = new AnchorPane();
            final Text dropText1 = new Text("↑");
             dropText1.setId("Text_Drop");
             dropText1.setLayoutX(169.0D);
             dropText1.setLayoutY(106.0D);
            final Text dropText2 = new Text("Drag & Drop here");
             dropText2.setId("Text_Drop");
             dropText2.setLayoutX(127.0D);
             dropText2.setLayoutY(123.0D);
            final AnchorPane fileDropArea = new AnchorPane(dropText1, dropText2);
             fileDropArea.setId("AnchorPane_DropArea");
             fileDropArea.setPrefHeight(220.0D);
             fileDropArea.setPrefWidth(350.0D);
             fileDropArea.setLayoutX(14.0D);
             fileDropArea.setLayoutY(42.0D);
             fileDropArea.setStyle("-fx-background-color: #D3D3D3;");
            final MFXTextField midPathField = new MFXTextField();
             midPathField.setId("TextField_MPath");
             midPathField.setLayoutX(14.0D);
             midPathField.setLayoutY(17.0D);
             midPathField.setPrefHeight(25.0D);
             midPathField.setPrefWidth(285.0D);
             midPathField.setPromptText("path");
             midPathField.setFloatingText("path");
             midPathField.setFloatMode(FloatMode.BORDER);
            final MFXButton pathReset = new MFXButton();
            pathReset.setId("Button_Reset");
             pathReset.setLayoutX(300.0D);
             pathReset.setLayoutY(17.0D);
             pathReset.setPrefHeight(25.0D);
             pathReset.setPrefWidth(65.0D);
             pathReset.setText("reset");
             pathReset.setButtonType(ButtonType.FLAT);
            final MFXButton playButton = new MFXButton();
            playButton.setId("Button_Play");
             playButton.setLayoutX(13.0D);
             playButton.setLayoutY(344.0D);
             playButton.setPrefHeight(20.0D);
             playButton.setPrefWidth(175.0D);
             playButton.setText("Play");
             playButton.setButtonType(ButtonType.FLAT);
            final MFXButton stopButton = new MFXButton();
            stopButton.setId("Button_Stop");
             stopButton.setLayoutX(189.0D);
             stopButton.setLayoutY(344.0D);
             stopButton.setPrefHeight(20.0D);
             stopButton.setPrefWidth(175.0D);
             stopButton.setText("Stop");
             stopButton.setButtonType(ButtonType.FLAT);
            final MFXTextField inputDelay = new MFXTextField();
            inputDelay.setId("TextField_Input");
             inputDelay.setLayoutX(189.0D);
             inputDelay.setLayoutY(310.0D);
             inputDelay.setPrefHeight(25.0D);
             inputDelay.setPrefWidth(175.0D);
             inputDelay.setFloatingText("delay (sec)");
             inputDelay.setFloatMode(FloatMode.BORDER);
            final MFXTextField windowName = new MFXTextField(ConfigHolder.configs.getWindowName());
            inputDelay.setId("TextField_WName");
             windowName.setLayoutX(189.0D);
             windowName.setLayoutY(274.0D);
             windowName.setPrefHeight(25.0D);
             windowName.setPrefWidth(175.0D);
             windowName.setPromptText("window name");
             windowName.setFloatingText("window name");
             windowName.setFloatMode(FloatMode.BORDER);
            final MFXCheckbox useHighPrecision = new MFXCheckbox();
             useHighPrecision.setId("CheckBox_useHP");
             useHighPrecision.setLayoutX(15.0D);
             useHighPrecision.setLayoutY(314.0D);
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

        // addStyleSheetAll(MUIView.class.getResource("View.css").toExternalForm(), BASE.getChildren().toArray(Parent[]::new));
    }

    public Pane getRootPane() {
        return ROOT;
    }

    private static void addStyleSheetAll(String style, Parent[] node) {
        Arrays.stream(node)
              .forEach(n -> n.getStylesheets().add(style));
    }

}
