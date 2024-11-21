package com.kmidiplayer.gui;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.Cache;
import com.kmidiplayer.midi.util.MidiFileChecker;
import com.kmidiplayer.midi.util.TrackInfo;
import com.kmidiplayer.util.ResourceLocation;

import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

/**
 * MVCのC UIのコンポーネントに対するアクションで実行する内容を書いておく
 */
public class MUIController {

    private final static Logger LOGGER = LogManager.getLogger("[MUI-Controller]");

    private final MUIModel model;

    private final List<Runnable> terminations;

    MUIController(MUIView view, Stage stage) {
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
        if (isExistedMidiFile(model.getPathFieldText())) {
            updatePlayers();
        }
    }

    Constraint getIntConstraint(StringProperty targetProperty) {
        return Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setCondition(Bindings.createBooleanBinding(
                () -> isInt(targetProperty.get()),
                targetProperty
            ))
            .get();
    }

    Constraint getPositiveIntConstraint(StringProperty targetProperty) {
        return Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setCondition(Bindings.createBooleanBinding(
                () -> isPositiveInt(targetProperty.get()),
                targetProperty
            ))
            .get();
    }

    // これ見...パクった
    // https://github.com/palexdev/MaterialFX/blob/main/demo/src/main/java/io/github/palexdev/materialfx/demo/controllers/TextFieldsController.java
    Constraint getLengthConstraint(StringProperty targetProperty) {
        return Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setCondition(targetProperty.length().greaterThan(0))
            .get();
    }

    Constraint getExistedMidiFileConstraint(StringProperty targetProperty) {
        return Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setCondition(Bindings.createBooleanBinding(
                () -> isExistedMidiFile(targetProperty.get()),
                targetProperty
            ))
            .get();
    }

    private static boolean isExistedMidiFile(String str) {
        final File file = new File(str);
        return (file).exists() && MidiFileChecker.isValid(file);
    }

    private static boolean isInt(String str) {
        final ParsePosition pos = new ParsePosition(0);
        NumberFormat.getIntegerInstance().parse(str, pos);
        return str.length() == pos.getIndex();
    }

    private static boolean isPositiveInt(String str) {
        return isInt(str) && str.length()!=0 && 0 < Integer.valueOf(str);
    }

    ChangeListener<Boolean> buildValidListener(MFXTextField control) {
        return (new ControlListener(control)).getListener();
    }

    /**
     * 無名クラスをどうしても使いたくなかったためinner classで代替
     */
    private static class ControlListener {

        private final MFXTextField mfxTextField;

        private final ChangeListener<Boolean> listenerMethod;

        private static final String INVALID_CSS = ResourceLocation.CSS_INVALID.toURL().toExternalForm();

        private ControlListener(MFXTextField mfxTextField) {
            this.mfxTextField = mfxTextField;
            listenerMethod = this::validListener;
        }

        private void validListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!oldValue && newValue) { // invalid -> valid
                mfxTextField.getStylesheets().remove(INVALID_CSS);
            }
            if (oldValue && !newValue) { // valid -> invalid
                mfxTextField.getStylesheets().add(INVALID_CSS);
            }
        }

        private ChangeListener<Boolean> getListener() {
            return listenerMethod;
        }
    }

    private void updatePlayers() {
        model.generatePlayer();
        model.clearSelectedHolder();
        if (model.isPlayerValid()) {
            model.addToSelectorHolderAllAndRefresh(generateTrackSelectToggleButton(model.getTrackInfo()));
        } else {
            model.setPlayButtonDisable(true);
        }
    }

    private Node[] generateTrackSelectToggleButton(TrackInfo[] trackInfos) {

        final MFXToggleButton[] selectorToggleButtons = new MFXToggleButton[trackInfos.length];

        final int maxLengthOfNoteCount = Stream.of(trackInfos).map(TrackInfo::getNotes).map(String::valueOf).mapToInt(String::length).max().orElse(-1);
        if (maxLengthOfNoteCount < 0) { throw new IllegalArgumentException(); } // トラック情報のノート数の文字数が負の数になる場合はトラック情報がおかしい。

        for(int i=0; i<trackInfos.length; i++) {
            selectorToggleButtons[i] = new MFXToggleButton();
            selectorToggleButtons[i].setText(
                "Notes: "
                .concat(" ".repeat(maxLengthOfNoteCount - String.valueOf(trackInfos[i].getNotes()).length()))
                .concat(String.valueOf(trackInfos[i].getNotes()))
                .concat(", ")
                .concat(TrackInfo.getInstrumentFromProgramChange(trackInfos[i].getProgramChange())));
            selectorToggleButtons[i].setId(String.valueOf(i));
            selectorToggleButtons[i].setOnAction(this::generatedToggleOnAction);
        }

        return selectorToggleButtons;
    }

    private void generatedToggleOnAction(ActionEvent event) {
        model.setPlayButtonEnableWhenToggleButtonEnabled();
        event.consume();
    }

    void pathReset_onAction(ActionEvent event) {
        model.clearSelectedHolder();
        model.setPath("");
    }

    void playButton_onAction(ActionEvent event) {
        model.play();
        model.setPlayButtonDisable(true);
        model.setStopButtonDisable(false);
    }

    void stopButton_onAction(ActionEvent event) {
        LOGGER.info("task is cancelled!");
        model.stop();
        model.setPlayButtonDisable(false);
        model.setStopButtonDisable(true);
    }

    ObservableList<String> getCacheData() {
        return Cache.getCache(); // ただのラッパー
    }

    MUIModel getModel() {
        return model;
    }
}
