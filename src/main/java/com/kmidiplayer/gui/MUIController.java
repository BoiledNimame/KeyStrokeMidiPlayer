package com.kmidiplayer.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.Cache;
import com.kmidiplayer.midi.util.MidiFileChecker;
import com.kmidiplayer.midi.util.TrackInfo;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

/**
 * MVCのC UIのコンポーネントに対するアクションで実行する内容を書いておく
 */
public class MUIController {

    private final static Logger LOGGER = LogManager.getLogger("[MUI-Controller]");

    private final Stage stage;
    private final MUIModel model;

    private final List<Runnable> terminations;

    MUIController(MUIView view, Stage stage) {

        this.stage = stage;
        model = new MUIModel(view);

        Cache.init();

        stage.showingProperty().addListener(this::termination);
        terminations = new ArrayList<>();

        terminations.add(model::stop);
        terminations.add(model::shutdown);
        terminations.add(() -> Cache.toCache(model.getPathFieldItem()));

    }

    /**
     * 親Stageのclose()を呼んでもsetOnCloseRequestが呼ばれない/ひとつしか登録できない という理由で終了処理は分離
     * 参考: https://torutk.hatenablog.jp/entry/20170613/p1
     */
    private void termination(ObservableValue<? extends Boolean> o, Boolean a, Boolean b) {
        if (a && !b) {
            // ウィンドウが閉じた直後に行われる終了処理
            terminations.forEach(Runnable::run);
        }
    }

    void closeButton_onAction(ActionEvent event) {
        Platform.exit(); // これでもterminationsはしっかりと呼ばれる
    }

    // 非常に不服
    // 参考: https://gist.github.com/jewelsea/2658491
    private double posOffsetX;
    private double posOffsetY;

    // クリック時にのみ更新される
    void titleBar_onMousePressed(MouseEvent event) {
        posOffsetX = stage.getX() - event.getScreenX();
        posOffsetY = stage.getY() - event.getScreenY();
    }

    // 追従させる
    void titleBar_onMouseDragged(MouseEvent event) {
        stage.setX(event.getScreenX() + posOffsetX);
        stage.setY(event.getScreenY() + posOffsetY);
    }

    // 入力プレビューウィンドウ版
    private double posNOffsetX;
    private double posNOffsetY;

    // クリック時にのみ更新される
    void NUITitleBar_onMousePressed(MouseEvent event) {
        posNOffsetX = model.getNotePrevStage().getX() - event.getScreenX();
        posNOffsetY = model.getNotePrevStage().getY() - event.getScreenY();
    }

    // 追従させる
    void NUITitleBar_onMouseDragged(MouseEvent event) {
        model.getNotePrevStage().setX(event.getScreenX() + posNOffsetX);
        model.getNotePrevStage().setY(event.getScreenY() + posNOffsetY);
    }

    void fileDropArea_dragOver(DragEvent event) {
        if (event.getGestureSource() != event.getSource()
            && event.getDragboard().hasFiles()
            && event.getDragboard().getFiles().stream().noneMatch(f -> !MidiFileChecker.isValid(f)) ){
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    void fileDropArea_dragDropped(DragEvent event) {
        final Dragboard db = event.getDragboard();
        final boolean isDBhasFile = db.hasFiles();
        if (isDBhasFile){
            Objects.requireNonNull(db.getFiles());
            final List<File> dropped_Files = db.getFiles();
            if (!dropped_Files.isEmpty()) {
                dropped_Files.stream()
                    .filter(MidiFileChecker::isValid)
                    .findFirst()
                    .ifPresent(a -> model.setPath(a.getAbsolutePath()));
                dropped_Files.stream()
                    .filter(MidiFileChecker::isValid)
                    .forEach(a -> model.addItemIfNotContains(a.getAbsolutePath()));
            }
        }
        event.setDropCompleted(isDBhasFile);
        event.consume();
    }

    void pathTextListener(ObservableValue<? extends String> v, String o, String n) {
        if (Validator.isExistedMidiFile(model.getPathFieldText())) {
            updatePlayers();
        }
    }

    private void updatePlayers() {
        model.generatePlayer();
        model.clearSelectedHolder();
        if (model.isPlayerValid()) {
            model.addToSelectorHolderAllAndRefresh(generateTrackSelectToggleButton(model.getTrackInfo()));
        }
        model.enablePlayButtonWhenAllValidatorValid();
    }

    private Node[] generateTrackSelectToggleButton(TrackInfo[] trackInfos) {

        final MFXToggleButton[] selectorToggleButtons = new MFXToggleButton[trackInfos.length];

        final int maxLengthOfNoteCount = Stream.of(trackInfos)
                                               .map(TrackInfo::getNotes)
                                               .map(String::valueOf)
                                               .mapToInt(String::length)
                                               .max()
                                               .orElseThrow(IllegalArgumentException::new); // トラック情報のノート数の文字数が負の数になる場合はトラック情報がおかしい。

        for(int i=0; i<trackInfos.length; i++) {
            selectorToggleButtons[i] = new MFXToggleButton();
            selectorToggleButtons[i].setText(
                "Notes: "
                .concat(" ".repeat(maxLengthOfNoteCount - String.valueOf(trackInfos[i].getNotes()).length()))
                .concat(String.valueOf(trackInfos[i].getNotes()))
                .concat(", ")
                .concat(TrackInfo.getInstrumentFromProgramChange(trackInfos[i].getProgramChange())));
            selectorToggleButtons[i].setId(String.valueOf(i));
            selectorToggleButtons[i].selectedProperty().addListener((x) -> model.enablePlayButtonWhenAllValidatorValid());
        }

        return selectorToggleButtons;
    }

    void pathReset_onAction(ActionEvent event) {
        model.clearSelectedHolder();
        model.setPath("");
        model.enablePlayButtonWhenAllValidatorValid();
    }

    void playButton_onAction(ActionEvent event) {
        model.play();
        model.enablePlayButtonWhenAllValidatorValid();
        model.setStopButtonDisable(false);
    }

    void stopButton_onAction(ActionEvent event) {
        LOGGER.info("task is cancelled!");
        model.stop();
        model.enablePlayButtonWhenAllValidatorValid();
        model.setStopButtonDisable(true);
    }

    void prevButton_onAction(ActionEvent event) {
        model.setPrevButtonDisable(true);
        model.showKeyInputPreviewUI();
    }

    Runnable getPlayButtonEnablerWhichValidatedBy(Supplier<Boolean> isValidSupplier) {
        model.addValidator(isValidSupplier);
        return model::enablePlayButtonWhenAllValidatorValid;
    }

    ObservableList<String> getCacheData() {
        return Cache.getCache(); // ただのラッパー
    }

    MUIModel getModel() {
        return model;
    }
}
