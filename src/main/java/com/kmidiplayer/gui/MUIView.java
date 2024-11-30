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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class MUIView {

    private final double HEIGHT = 384.0D;
    private final double WIDTH  = 660.0D;
    public double getWindowHeight() {return HEIGHT + titleBarHeight; }
    public double getWindowWidth() { return WIDTH; }

    private final String TITLE = I18n.TITLE.getDefault();
    public String getTitle() { return TITLE; }

    private final Image ICON;
    public Image getIcon() { return ICON; }

    private final String DEFAULT_STYLE;
    private final String CUSTOM_STYLE;

    private final Stage stage;

    final MUIController controller;

    final VBox windowWrapper;
    final AnchorPane titleBar;
    private final double titleBarHeight = 30.0D;

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

        // Title Bar
        titleBar = new AnchorPane();
        titleBar.setId("TitleBar");
        titleBar.setPrefWidth(WIDTH);
        titleBar.setPrefHeight(titleBarHeight);
        titleBar.getStylesheets().add(CUSTOM_STYLE); // font関連が狂うので予め追加してしまう
        // Title Bar Items
            // Icon
            final ImageView iconImage = new ImageView(ICON);
                final Double iconImageOffset = 7.0D;
                iconImage.setFitWidth(titleBarHeight - (iconImageOffset *2));
                iconImage.setFitHeight(titleBarHeight - (iconImageOffset *2));
                AnchorPane.setTopAnchor(iconImage, iconImageOffset);
                AnchorPane.setLeftAnchor(iconImage, iconImageOffset);
            // title
            final Text titleText = new Text(TITLE);
                titleText.setId("Text_Title");
                AnchorPane.setLeftAnchor(titleText, titleBarHeight);
                AnchorPane.setTopAnchor(titleText, (titleBarHeight / 2) - (titleText.getFont().getSize() * 0.75));
            // close button (x)
            final MFXButton closeButton = new MFXButton();
                closeButton.setId("Button_Close");
                closeButton.setText("x");
                closeButton.setPrefWidth(titleBarHeight * 1.5D);
                closeButton.setPrefHeight(titleBarHeight);
                closeButton.setOnAction(controller::closeButton_onAction);
                AnchorPane.setRightAnchor(closeButton, 0D);
        titleBar.getChildren().addAll(iconImage, titleText, closeButton);
        TitleBarDragHandler.build(stage).handle(titleBar);

        // TitleBar & UI Wrapper
        windowWrapper = new VBox();
        windowWrapper.setPrefWidth(WIDTH);
        windowWrapper.setPrefHeight(titleBarHeight + HEIGHT);

        // UI
        root = new AnchorPane();
        root.setId("Root");
        root.setPrefWidth(WIDTH);
        root.setPrefHeight(HEIGHT);
            // ドラッグ&ドロップするエリアのテキスト
            final Text dropText1 = new Text();
                dropText1.setId("Text_Drop");
                dropText1.setText(I18n.DRAGDROP_LABEL_1.getDefault());
                dropText1.setLayoutX(169.0D);
                dropText1.setLayoutY(76.0D);
            final Text dropText2 = new Text();
                dropText2.setId("Text_Drop");
                dropText2.setText(I18n.DRAGDROP_LABEL_2.getDefault());
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
                pathInput.setPrefWidth(630.0D);
                pathInput.setFloatingText(I18n.COMBOBOX_PATH.getDefault());
                pathInput.setFloatMode(FloatMode.BORDER);
                pathInput.setEditable(true);
                pathInput.setItems(controller.getCacheData());
                pathInput.textProperty().addListener(controller::pathTextListener);
                    pathInput.getValidator().constraint(Validator.getExistedMidiFileConstraint(pathInput.textProperty()));
                    pathInput.getValidator().validProperty().addListener(Validator.buildValidListener(pathInput, Validator::setValid, Validator::setInvalid));
                    pathInput.getValidator().validProperty().addListener(Validator.buildValidListener(controller.getPlayButtonEnablerWhichValidatedBy(() -> pathInput.getValidator().validProperty().get())));
            // 再生ボタン
            playButton = new MFXButton();
            playButton.setId("Button_Play");
                playButton.setLayoutX(13.0D);
                playButton.setLayoutY(339.0D);
                playButton.setPrefHeight(30.0D);
                playButton.setPrefWidth(175.0D);
                playButton.setText(I18n.BUTTON_PLAY.getDefault());
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
                stopButton.setText(I18n.BUTTON_STOP.getDefault());
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
                prevButton.setText(I18n.BUTTON_PREV.getDefault());
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
                initialDelayInput.setFloatingText(I18n.TEXTFIELD_INITIAL_DELAY.getDefault());
                initialDelayInput.setFloatMode(FloatMode.BORDER);
                    initialDelayInput.getValidator()
                        .constraint(Validator.getPositiveIntConstraint(initialDelayInput.textProperty()))
                        .constraint(Validator.getLengthConstraint(initialDelayInput.textProperty()));
                    initialDelayInput.getValidator().validProperty().addListener(Validator.buildValidListener(initialDelayInput, Validator::setValid, Validator::setInvalid));
                    initialDelayInput.getValidator().validProperty().addListener(Validator.buildValidListener(controller.getPlayButtonEnablerWhichValidatedBy(() -> initialDelayInput.getValidator().validProperty().get())));
            // 入力先ウィンドウタイトルの入力フィールド
            windowNameInput = new MFXTextField(Options.configs.getWindowName());
            windowNameInput.setId("TextField_WName");
                windowNameInput.setLayoutX(13.0);
                windowNameInput.setLayoutY(244.0D);
                windowNameInput.setPrefHeight(25.0D);
                windowNameInput.setPrefWidth(175.0D);
                windowNameInput.setPromptText(I18n.TEXTFIELD_WINDOW_NAME.getDefault());
                windowNameInput.setFloatingText(I18n.TEXTFIELD_WINDOW_NAME.getDefault());
                windowNameInput.setFloatMode(FloatMode.BORDER);
                windowNameInput.setDisable(Options.configs.getIsMock());
                    windowNameInput.getValidator().constraint(Validator.getLengthConstraint(windowNameInput.textProperty()));
                    windowNameInput.getValidator().validProperty().addListener(Validator.buildValidListener(windowNameInput, Validator::setValid, Validator::setInvalid));
                    windowNameInput.getValidator().validProperty().addListener(Validator.buildValidListener(controller.getPlayButtonEnablerWhichValidatedBy(() -> windowNameInput.getValidator().validProperty().get())));
            // 音階オフセットの入力フィールド
            noteNumberOffsetInput = new MFXTextField(String.valueOf(Options.configs.getNoteOffset()));
            noteNumberOffsetInput.setId("TextField_NOTEOFFSET");
                noteNumberOffsetInput.setLayoutX(189.0D);
                noteNumberOffsetInput.setLayoutY(244.0D);
                noteNumberOffsetInput.setPrefHeight(25.0D);
                noteNumberOffsetInput.setPrefWidth(175.0D);
                noteNumberOffsetInput.setFloatingText(I18n.TEXTFIELD_NOTE_OFFSET.getDefault());
                noteNumberOffsetInput.setFloatMode(FloatMode.BORDER);
                    noteNumberOffsetInput.getValidator()
                        .constraint(Validator.getIntConstraint(noteNumberOffsetInput.textProperty()))
                        .constraint(Validator.getLengthConstraint(noteNumberOffsetInput.textProperty()))
                        .constraint(Validator.getCollectInRangeOfNoteNumberOffset(noteNumberOffsetInput.textProperty()));
                    noteNumberOffsetInput.getValidator().validProperty().addListener(Validator.buildValidListener(noteNumberOffsetInput, Validator::setValid, Validator::setInvalid));
                    noteNumberOffsetInput.getValidator().validProperty().addListener(Validator.buildValidListener(controller.getPlayButtonEnablerWhichValidatedBy(() -> noteNumberOffsetInput.getValidator().validProperty().get())));
                    final VBox offsetButtonsWrapper = new VBox();
                        offsetButtonsWrapper.setPrefWidth(noteNumberOffsetInput.getPrefHeight() * 1.5D);
                        offsetButtonsWrapper.setPrefHeight(noteNumberOffsetInput.getPrefHeight() * 1.5D);
                        offsetButtonsWrapper.setLayoutX(noteNumberOffsetInput.getLayoutX() + noteNumberOffsetInput.getPrefWidth() - offsetButtonsWrapper.getPrefWidth());
                        offsetButtonsWrapper.setLayoutY(noteNumberOffsetInput.getLayoutY());
                        final MFXButton offsetInputNumberUp = new MFXButton();
                            offsetInputNumberUp.setId("Button_Offset");
                            offsetInputNumberUp.setText("∧");
                            setMinSizeTo1px(offsetInputNumberUp);
                            offsetInputNumberUp.setPrefWidth(offsetButtonsWrapper.getPrefWidth());
                            offsetInputNumberUp.setPrefHeight(offsetButtonsWrapper.getPrefHeight() / 2D);
                                offsetInputNumberUp.setOnAction(controller::offsetInputNumberUp_OnAction);
                        final MFXButton offsetInputNumberDown = new MFXButton();
                            setMinSizeTo1px(offsetInputNumberDown);
                            offsetInputNumberDown.setId("Button_Offset");
                            offsetInputNumberDown.setText("∧");
                            offsetInputNumberDown.setRotate(180D);
                            offsetInputNumberDown.setPrefWidth(offsetButtonsWrapper.getPrefWidth());
                            offsetInputNumberDown.setPrefHeight(offsetButtonsWrapper.getPrefHeight() / 2D);
                                offsetInputNumberDown.setOnAction(controller::offsetInputNumberDown_OnAction);
                    offsetButtonsWrapper.getChildren().addAll(offsetInputNumberUp, offsetInputNumberDown);
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
                trackSelectorLabel.setText(I18n.LABEL_TRACKS.getDefault());
                AnchorPane.setLeftAnchor(trackSelectorLabel, WIDTH - ( trackSelectorHolderWrapperPane.getPrefWidth() + 15.0D));
                AnchorPane.setBottomAnchor(trackSelectorLabel, trackSelectorHolderWrapperPane.getPrefHeight() + 15.0D);
        root.getChildren().addAll(fileDropArea, pathInput, playButton, stopButton, prevButton, initialDelayInput, windowNameInput, noteNumberOffsetInput, offsetButtonsWrapper, trackSelectorLabel, trackSelectorHolderWrapperPane);

        windowWrapper.getStylesheets().add(DEFAULT_STYLE);
        windowWrapper.getStylesheets().add(CUSTOM_STYLE);

        windowWrapper.getChildren().addAll(titleBar, root);
    }

    private static <N extends Node> void setMinSizeTo1px(N node) {
        node.minWidth(0D);
        node.minHeight(0D);
    }

    Stage kInPreviewStage;

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

        kInPreviewStage = new Stage();

        final NoteUIView keyInputPreviewUIView = new NoteUIView(this);

        // Title Bar
        final double NUITitleBarHeight = titleBarHeight * 0.75D;
        final AnchorPane NUITitleBar = new AnchorPane();
        NUITitleBar.setId("TitleBar");
        NUITitleBar.setPrefWidth(WIDTH);
        NUITitleBar.setMinHeight(0D);
        NUITitleBar.setPrefHeight(NUITitleBarHeight);
        NUITitleBar.getStylesheets().add(CUSTOM_STYLE); // font関連が狂うので予め追加してしまう
        // Title Bar Items
            // close button (x)
            final MFXButton NUICloseButton = new MFXButton();
                NUICloseButton.setId("Button_Close");
                NUICloseButton.setText("x");
                NUICloseButton.setPrefWidth(NUITitleBarHeight * 1.75D);
                NUICloseButton.setMinHeight(0D);
                NUICloseButton.setPrefHeight(NUITitleBarHeight);
                NUICloseButton.setOnAction((x) -> kInPreviewStage.close());
                AnchorPane.setRightAnchor(NUICloseButton, 0D);
        NUITitleBar.getChildren().add(NUICloseButton);
        TitleBarDragHandler.build(kInPreviewStage).handle(NUITitleBar);
        // TitleBar & UI Wrapper
        final VBox NUIWindowWrapper = new VBox();
        NUIWindowWrapper.setPrefWidth(keyInputPreviewUIView.getRoot().getPrefWidth());
        NUIWindowWrapper.setPrefHeight(NUITitleBar.getPrefHeight() + keyInputPreviewUIView.getRoot().getPrefHeight());
        NUIWindowWrapper.getChildren().addAll(NUITitleBar, keyInputPreviewUIView.getRoot());

        final Scene keyInputPreviewUIScene = new Scene(NUIWindowWrapper, NUIWindowWrapper.getPrefWidth(), NUIWindowWrapper.getPrefHeight());

        kInPreviewStage.initStyle(StageStyle.UNDECORATED);
        kInPreviewStage.setScene(keyInputPreviewUIScene);
        kInPreviewStage.setResizable(false);
        kInPreviewStage.initOwner(stage);

        kInPreviewStage.showingProperty().addListener(
            (o, a, b) -> {
                if (a && !b) {
                    prevButton.setDisable(false);
                }
            }
        );

    }

    MUIController getcontroller() {
        return controller;
    }

    public Pane getRootPane() {
        return windowWrapper;
    }
}
