package com.kmidiplayer.gui;

import com.kmidiplayer.config.Options;
import com.kmidiplayer.lang.I18n;
import com.kmidiplayer.util.ResourceLocation;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class MUIView {

    private final double HEIGHT = 384.0D;
    public double getHeight() { return HEIGHT; }
    private final double WIDTH  = 660.0D;
    public double getWidth() { return WIDTH; }

    private final String TITLE = I18n.TITLE.getDefault();
    public String getTitle() { return TITLE; }

    private final Image ICON;
    public Image getIcon() { return ICON; }

    private final String DEFAULT_STYLE;
    private final String CUSTOM_STYLE;

    private final Stage stage;

    final MUIController controller;

    final AnchorPane root;
    final MFXComboBox<String> pathInput;
    final VBox trackHolderPane;
    final MFXTextField windowNameInput;
    final MFXTextField initialDelayInput;
    final MFXTextField noteNumberOffsetInput;
    final MFXButton playButton;
    final MFXButton stopButton;
    final MFXButton prevButton;

    public MUIView(Stage stage, String defaultStyle) {

        this.stage = stage;
        controller = new MUIController(this, stage);

        DEFAULT_STYLE = defaultStyle;
        CUSTOM_STYLE = Objects.requireNonNull(ResourceLocation.CSS_CUSTOM.toURL()).toExternalForm();

        ICON = new Image(getClass().getResource("icon.png").toString());

        root = new AnchorPane();
        root.setId("Root");
            // ドラッグ&ドロップするエリアのテキスト
            final Text dropText1 = new Text("↑");
                dropText1.setId("Text_Drop");
                dropText1.setLayoutX(169.0D);
                dropText1.setLayoutY(76.0D);
            final Text dropText2 = new Text("Drag & Drop here");
                dropText2.setId("Text_Drop");
                dropText2.setLayoutX(127.0D);
                dropText2.setLayoutY(93.0D);
            // ドラッグ&ドロップできるエリア
            final AnchorPane fileDropArea = new AnchorPane(dropText1, dropText2);
                fileDropArea.setId("AnchorPane_DropArea");
                fileDropArea.setPrefHeight(155.0D);
                fileDropArea.setPrefWidth(350.0D);
                fileDropArea.setLayoutX(14.0D);
                fileDropArea.setLayoutY(72.0D);
                    fileDropArea.setOnDragOver(controller::fileDropArea_dragOver);
                    fileDropArea.setOnDragDropped(controller::fileDropArea_dragDropped);
            // 入力ファイルの絶対パスが出たり書いたり選択できたりするやつ
            pathInput = new MFXComboBox<>();
                pathInput.setId("ComboBox_Paths");
                pathInput.setLayoutX(14.0D);
                pathInput.setLayoutY(17.0D);
                pathInput.setPrefHeight(38.0D);
                pathInput.setPrefWidth(565.0D);
                pathInput.setFloatingText("path");
                pathInput.setFloatMode(FloatMode.BORDER);
                pathInput.setEditable(true);
                pathInput.setItems(controller.getCacheData());
                pathInput.textProperty().addListener(controller::pathTextListener);
                    pathInput.getValidator().constraint(Validator.getExistedMidiFileConstraint(pathInput.textProperty()));
                    pathInput.getValidator().validProperty().addListener(Validator.buildValidListener(pathInput, this::ifValid, this::ifInvalid));
                    pathInput.getValidator().validProperty().addListener(Validator.buildValidListener(controller.getPlayButtonEnablerWhichValidatedBy(() -> pathInput.getValidator().validProperty().get())));
            // 入力ファイルの絶対パスをリセットするやつ(いる?)
            final MFXButton pathReset = new MFXButton();
            pathReset.setId("Button_Reset");
                pathReset.setLayoutX(pathInput.getLayoutX() + pathInput.getPrefWidth());
                pathReset.setLayoutY(17.0D);
                pathReset.setPrefHeight(37.5D);
                pathReset.setPrefWidth(65.0D);
                pathReset.setText("reset");
                pathReset.setButtonType(ButtonType.FLAT);
                    pathReset.setOnAction(controller::pathReset_onAction);
            // 再生ボタン
            playButton = new MFXButton();
            playButton.setId("Button_Play");
                playButton.setLayoutX(13.0D);
                playButton.setLayoutY(339.0D);
                playButton.setPrefHeight(30.0D);
                playButton.setPrefWidth(175.0D);
                playButton.setText("Play");
                playButton.setButtonType(ButtonType.FLAT);
                    playButton.setOnAction(controller::playButton_onAction);
                playButton.setDisable(true);
            // 停止ボタン
            stopButton = new MFXButton();
            stopButton.setId("Button_Stop");
                stopButton.setLayoutX(189.0D);
                stopButton.setLayoutY(339.0D);
                stopButton.setPrefHeight(30.0D);
                stopButton.setPrefWidth(175.0D);
                stopButton.setText("Stop");
                stopButton.setButtonType(ButtonType.FLAT);
                    stopButton.setOnAction(controller::stopButton_onAction);
                stopButton.setDisable(true);
            // 入力プレビューを表示するウィンドウを出すボタン
            prevButton = new MFXButton();
            prevButton.setId("Button_Prev");
                prevButton.setLayoutX(189.0D);
                prevButton.setLayoutY(290.0D);
                prevButton.setPrefHeight(37.5D);
                prevButton.setPrefWidth(175.0D);
                prevButton.setText("keyInputPrevew");
                prevButton.setButtonType(ButtonType.FLAT);
                    prevButton.setOnAction(controller::prevButton_onAction);
                prevButton.setDisable(Options.configs.useNoteUI());
            // 再生遅延の入力フィールド
            initialDelayInput = new MFXTextField(String.valueOf(Options.configs.getInitialDelay()));
            initialDelayInput.setId("TextField_Input");
                initialDelayInput.setLayoutX(13.0);
                initialDelayInput.setLayoutY(290.0D);
                initialDelayInput.setPrefHeight(25.0D);
                initialDelayInput.setPrefWidth(175.0D);
                initialDelayInput.setFloatingText("delay (milliseconds)");
                initialDelayInput.setFloatMode(FloatMode.BORDER);
                    initialDelayInput.getValidator()
                        .constraint(Validator.getPositiveIntConstraint(initialDelayInput.textProperty()))
                        .constraint(Validator.getLengthConstraint(initialDelayInput.textProperty()));
                    initialDelayInput.getValidator().validProperty().addListener(Validator.buildValidListener(initialDelayInput, this::ifValid, this::ifInvalid));
                    initialDelayInput.getValidator().validProperty().addListener(Validator.buildValidListener(controller.getPlayButtonEnablerWhichValidatedBy(() -> initialDelayInput.getValidator().validProperty().get())));
            // 入力先ウィンドウタイトルの入力フィールド
            windowNameInput = new MFXTextField(Options.configs.getWindowName());
            windowNameInput.setId("TextField_WName");
                windowNameInput.setLayoutX(13.0);
                windowNameInput.setLayoutY(244.0D);
                windowNameInput.setPrefHeight(25.0D);
                windowNameInput.setPrefWidth(175.0D);
                windowNameInput.setPromptText("window name");
                windowNameInput.setFloatingText("window name");
                windowNameInput.setFloatMode(FloatMode.BORDER);
                windowNameInput.setDisable(Options.configs.getIsMock());
                    windowNameInput.getValidator().constraint(Validator.getLengthConstraint(windowNameInput.textProperty()));
                    windowNameInput.getValidator().validProperty().addListener(Validator.buildValidListener(windowNameInput, this::ifValid, this::ifInvalid));
                    windowNameInput.getValidator().validProperty().addListener(Validator.buildValidListener(controller.getPlayButtonEnablerWhichValidatedBy(() -> windowNameInput.getValidator().validProperty().get())));
            // 音階オフセットの入力フィールド
            noteNumberOffsetInput = new MFXTextField(String.valueOf(Options.configs.getNoteOffset()));
            noteNumberOffsetInput.setId("TextField_NOTEOFFSET");
                noteNumberOffsetInput.setLayoutX(189.0D);
                noteNumberOffsetInput.setLayoutY(244.0D);
                noteNumberOffsetInput.setPrefHeight(25.0D);
                noteNumberOffsetInput.setPrefWidth(175.0D);
                noteNumberOffsetInput.setFloatingText("NoteNumber Offset");
                noteNumberOffsetInput.setFloatMode(FloatMode.BORDER);
                    noteNumberOffsetInput.getValidator()
                        .constraint(Validator.getIntConstraint(noteNumberOffsetInput.textProperty()))
                        .constraint(Validator.getLengthConstraint(noteNumberOffsetInput.textProperty()))
                        .constraint(Validator.getCollectInRangeOfNoteNumberOffset(noteNumberOffsetInput.textProperty()));
                    noteNumberOffsetInput.getValidator().validProperty().addListener(Validator.buildValidListener(noteNumberOffsetInput, this::ifValid, this::ifInvalid));
                    noteNumberOffsetInput.getValidator().validProperty().addListener(Validator.buildValidListener(controller.getPlayButtonEnablerWhichValidatedBy(() -> noteNumberOffsetInput.getValidator().validProperty().get())));
            // トラック情報を含むトラックボタンのホルダー
            trackHolderPane = new VBox();
                trackHolderPane.setId("VBox_TrackHolder");
                trackHolderPane.setPrefHeight(0.0D);
                trackHolderPane.setPrefWidth(185.0D);
            // トラック情報を含むトラックボタンのホルダーのラッパー(トラックが多すぎる場合に備えてスクロールできるように)
            final MFXScrollPane trackSelectorHolderWrapperPane = new MFXScrollPane(trackHolderPane);
                trackSelectorHolderWrapperPane.setId("ScrollPane_HolderWrapper");
                trackSelectorHolderWrapperPane.setPrefHeight(295.0D);
                trackSelectorHolderWrapperPane.setPrefWidth(265.0D);
                AnchorPane.setRightAnchor(trackSelectorHolderWrapperPane, 15.0D);
                AnchorPane.setBottomAnchor(trackSelectorHolderWrapperPane, 15.0D);
            // トラック情報を含むトラックボタンのホルダーのテキスト
            final Label trackSelectorLabel = new Label();
                trackSelectorLabel.setId("Text_TSelector");
                trackSelectorLabel.setText("tracks");
                AnchorPane.setRightAnchor(trackSelectorLabel, trackSelectorHolderWrapperPane.getPrefWidth() - 20);
                AnchorPane.setBottomAnchor(trackSelectorLabel, trackSelectorHolderWrapperPane.getPrefHeight() + 15.0D);
        root.getChildren().addAll(fileDropArea, pathInput, pathReset, playButton, stopButton, prevButton, initialDelayInput, windowNameInput, noteNumberOffsetInput, trackSelectorLabel, trackSelectorHolderWrapperPane);

        root.getStylesheets().add(DEFAULT_STYLE);
        root.getStylesheets().add(CUSTOM_STYLE);

    }

    private Stage kInPreviewStage;

    public void showKeyInputPreviewUIView() {

        if (kInPreviewStage == null) {
            buildNoteUI();
        }

        // 位置を調整
        kInPreviewStage.setX(kInPreviewStage.getOwner().getX() - (kInPreviewStage.getWidth() / 2) + (kInPreviewStage.getOwner().getWidth() / 2));
        kInPreviewStage.setY(kInPreviewStage.getOwner().getY() + kInPreviewStage.getOwner().getHeight());

        kInPreviewStage.show();

    }

    private void buildNoteUI() {

        final NoteUIView keyInputPreviewUIView = new NoteUIView(this);
        final Scene keyInputPreviewUIScene = new Scene(keyInputPreviewUIView.getRoot(), keyInputPreviewUIView.getRoot().getPrefWidth(), keyInputPreviewUIView.getRoot().getPrefHeight());

        kInPreviewStage = new Stage();

        kInPreviewStage.setScene(keyInputPreviewUIScene);
        kInPreviewStage.setResizable(false);
        kInPreviewStage.initStyle(StageStyle.UTILITY);
        kInPreviewStage.initOwner(stage);

        kInPreviewStage.showingProperty().addListener(
            (o, a, b) -> {
                if (a && !b) {
                    prevButton.setDisable(false);
                }
            }
        );

    }

    private static final String INVALID_CSS = ResourceLocation.CSS_INVALID.toURL().toExternalForm();

    void ifValid(MFXTextField mfxTextField) {
        mfxTextField.getStylesheets().remove(INVALID_CSS);
    }

    void ifInvalid(MFXTextField mfxTextField) {
        mfxTextField.getStylesheets().add(INVALID_CSS);
    }

    MUIController getcontroller() {
        return controller;
    }

    public Pane getRootPane() {
        return root;
    }
}
