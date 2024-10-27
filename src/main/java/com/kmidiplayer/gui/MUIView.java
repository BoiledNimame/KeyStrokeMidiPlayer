package com.kmidiplayer.gui;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.util.Resource;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Objects;

public class MUIView {

    private final double HEIGHT = 384.0D;
    public double getHeight() { return HEIGHT; }
    private final double WIDTH  = 660.0D;
    public double getWidth() { return WIDTH; }

    private final String TITLE = "keystroke midifile player";
    public String getTitle() { return TITLE; }

    private final Image ICON;
    public Image getIcon() { return ICON; }

    private final String DEFAULT_STYLE;
    private final String CUSTOM_STYLE;

    private final AnchorPane ROOT;
    private final MFXComboBox<String> PATHBOX;
    private final VBox TRACK_HOLDER;
    private final MFXTextField WINDOW_NAME;
    private final MFXTextField INPUT_DELAY;
    private final MFXTextField NOTE_OFFSET;
    private final MFXCheckbox USE_HIGH_PRECISION;
    private final MFXButton PLAY_BUTTON;
    private final MFXButton STOP_BUTTON;

    public MUIView(Stage stage, String defaultStyle) {

        MUIController controller = new MUIController(this, stage);

        DEFAULT_STYLE = defaultStyle;
        CUSTOM_STYLE = Objects.requireNonNull(MUIView.class.getResource("View.css")).toExternalForm();

        ICON = new Image(Resource.getFIleURLAsString(Main.class, "images", "icon.png"));

        ROOT = new AnchorPane();
        ROOT.setId("Root");
            final Text dropText1 = new Text("↑");
             dropText1.setId("Text_Drop");
             dropText1.setLayoutX(169.0D);
             dropText1.setLayoutY(86.0D);
            final Text dropText2 = new Text("Drag & Drop here");
             dropText2.setId("Text_Drop");
             dropText2.setLayoutX(127.0D);
             dropText2.setLayoutY(103.0D);
            final AnchorPane fileDropArea = new AnchorPane(dropText1, dropText2);
             fileDropArea.setId("AnchorPane_DropArea");
             fileDropArea.setPrefHeight(170.0D);
             fileDropArea.setPrefWidth(350.0D);
             fileDropArea.setLayoutX(14.0D);
             fileDropArea.setLayoutY(57.0D);
              fileDropArea.setOnDragOver(controller::fileDropArea_dragOver);
              fileDropArea.setOnDragDropped(controller::fileDropArea_dragDropped);
            PATHBOX = new MFXComboBox<>();
             PATHBOX.setId("ComboBox_Paths"); // キャッシュ実装
             PATHBOX.setLayoutX(14.0D);
             PATHBOX.setLayoutY(17.0D);
             PATHBOX.setPrefHeight(38.0D);
             PATHBOX.setPrefWidth(285.0D);
             PATHBOX.setFloatingText("path");
             PATHBOX.setFloatMode(FloatMode.BORDER);
             PATHBOX.setEditable(true);
             PATHBOX.setItems(controller.getCacheData());
            final MFXButton pathReset = new MFXButton();
            pathReset.setId("Button_Reset");
             pathReset.setLayoutX(300.0D);
             pathReset.setLayoutY(17.0D);
             pathReset.setPrefHeight(37.5D);
             pathReset.setPrefWidth(65.0D);
             pathReset.setText("reset");
             pathReset.setButtonType(ButtonType.FLAT);
              pathReset.setOnAction(controller::pathReset_onAction);
            PLAY_BUTTON = new MFXButton();
            PLAY_BUTTON.setId("Button_Play");
             PLAY_BUTTON.setLayoutX(13.0D);
             PLAY_BUTTON.setLayoutY(339.0D);
             PLAY_BUTTON.setPrefHeight(30.0D);
             PLAY_BUTTON.setPrefWidth(175.0D);
             PLAY_BUTTON.setText("Play");
             PLAY_BUTTON.setButtonType(ButtonType.FLAT);
              PLAY_BUTTON.setOnAction(controller::playButton_onAction);
             PLAY_BUTTON.setDisable(true);
            STOP_BUTTON = new MFXButton();
            STOP_BUTTON.setId("Button_Stop");
             STOP_BUTTON.setLayoutX(189.0D);
             STOP_BUTTON.setLayoutY(339.0D);
             STOP_BUTTON.setPrefHeight(30.0D);
             STOP_BUTTON.setPrefWidth(175.0D);
             STOP_BUTTON.setText("Stop");
             STOP_BUTTON.setButtonType(ButtonType.FLAT);
              STOP_BUTTON.setOnAction(controller::stopButton_onAction);
             STOP_BUTTON.setDisable(true);
            INPUT_DELAY = new MFXTextField(String.valueOf(ConfigHolder.configs.getInitialDelay()));
            INPUT_DELAY.setId("TextField_Input");
             INPUT_DELAY.setLayoutX(13.0);
             INPUT_DELAY.setLayoutY(290.0D);
             INPUT_DELAY.setPrefHeight(25.0D);
             INPUT_DELAY.setPrefWidth(175.0D);
             INPUT_DELAY.setFloatingText("delay (milliseconds)");
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
            NOTE_OFFSET = new MFXTextField(String.valueOf(ConfigHolder.configs.getNoteOffset()));
            NOTE_OFFSET.setId("TextField_NOTEOFFSET");
             NOTE_OFFSET.setLayoutX(189.0D);
             NOTE_OFFSET.setLayoutY(244.0D);
             NOTE_OFFSET.setPrefHeight(25.0D);
             NOTE_OFFSET.setPrefWidth(175.0D);
             NOTE_OFFSET.setFloatingText("NoteNumber Offset");
             NOTE_OFFSET.setFloatMode(FloatMode.BORDER);
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
             trackSelectorHolderWrapperPane.setPrefWidth(260.0D);
             AnchorPane.setRightAnchor(trackSelectorHolderWrapperPane, 15.0D);
             AnchorPane.setBottomAnchor(trackSelectorHolderWrapperPane, 15.0D);
            final Label trackSelectorLabel = new Label();
             trackSelectorLabel.setId("Text_TSelector");
             trackSelectorLabel.setText("tracks");
             AnchorPane.setRightAnchor(trackSelectorLabel, trackSelectorHolderWrapperPane.getPrefWidth() - 20);
             AnchorPane.setTopAnchor(trackSelectorLabel, 20.0D);
        ROOT.getChildren().addAll(fileDropArea, PATHBOX, pathReset, PLAY_BUTTON, STOP_BUTTON, INPUT_DELAY, WINDOW_NAME, NOTE_OFFSET, USE_HIGH_PRECISION, trackSelectorLabel, trackSelectorHolderWrapperPane);

        ROOT.getStylesheets().add(DEFAULT_STYLE);
        ROOT.getStylesheets().add(CUSTOM_STYLE);
    }

    public Pane getRootPane() {
        return ROOT;
    }

    MFXComboBox<String> getPathField() {
        return PATHBOX;
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

    MFXTextField getNOTE_OFFSET() {
        return NOTE_OFFSET;
    }

    MFXCheckbox getUseHighPrecisionCheckBox() {
        return USE_HIGH_PRECISION;
    }

    MFXButton getPlayButton() {
        return PLAY_BUTTON;
    }

    MFXButton getStopButton() {
        return STOP_BUTTON;
    }
}
