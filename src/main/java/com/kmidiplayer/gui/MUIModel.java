package com.kmidiplayer.gui;

import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.midi.MidiFilePlayer;
import com.kmidiplayer.midi.util.TrackInfo;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Toggle;

public final class MUIModel {

    private final static Logger LOGGER = LogManager.getLogger("[MUI-Model]");

    private final static String EMPTY = "";

    private final MUIView view;

    MUIModel(MUIView view) {
        this.view = view;
        this.before = new LinkedList<>();
        this.after = new LinkedList<>();
        after.add(this::cleanUpUI);
        this.validators = new LinkedList<>();
        validators.add(this::isTrackHolderElementSelectedEvenOne);
    }

    final void setPath(String text) {
        if (EMPTY.equals(text) || text==null) {
            view.pathInput.clear();
        } else {
            view.pathInput.setText(text);
        }
    }

    private MidiFilePlayer player;

    final boolean isPlayerValid() {
        return player!=null && player.isValid();
    }

    final void generatePlayer() {
        if (!"".equals(getPathFieldText()) || player!=null && !player.isAlive()) {
            player = new MidiFilePlayer(Paths.get(getPathFieldText()).toFile());
            LOGGER.info(player.isValid() ? "File Loaded successfully." : "File cannot be read or is corrupt.");
        }
    }

    final TrackInfo[] getTrackInfo() {
        return player.getTrackInfos();
    }

    final void play() {
        if (player!=null && player.isValid()) {
            player.playThen(
                view.trackHolderPane.getChildren().stream()
                    .filter(p -> p instanceof MFXToggleButton)
                    .map(m -> (MFXToggleButton) m)
                    .filter(p -> p.selectedProperty().get())
                    .mapToInt(m -> Integer.parseInt(m.getId()))
                    .toArray(),
                EMPTY.equals(view.initialDelayInput.getText()) ? 0 : Integer.parseInt(view.initialDelayInput.getText()),
                Integer.parseInt(view.noteNumberOffsetInput.getText()),
                view.windowNameInput.getText(),
                view.highPrecisionCheckBox.selectedProperty().get(),
                this::before,
                this::after
            );
        }
    }

    final void stop() {
        if (player!=null && player.isAlive()) {
            player.stop();
        }
    }

    final void shutdown() {
        if (player!=null) {
            player.shutdown();
        }
    }

    final Supplier<MidiFilePlayer> getPlayerSupplier() {
        return () -> player;
    }

    final List<Runnable> before;

    final void addBeforePlay(Runnable task) {
        before.add(task);
    }

    final void before() {
        before.forEach(Runnable::run);
    }

    final List<Runnable> after;

    final void addAfterPlay(Runnable task) {
        after.add(task);
    }

    final void after() {
        after.forEach(Runnable::run);
    }

    final void cleanUpUI() {
        // 再生終了時の処理
        // トラックが選択されていればplayを有効化(stopは必ず無効に)
        view.playButton.setDisable(
            view.trackHolderPane.getChildren().stream()
                .filter(p -> p instanceof Toggle)
                .map(m -> (Toggle) m)
                .noneMatch(p -> p.selectedProperty().get()));
        view.stopButton.setDisable(true);
    }

    final void addToSelectorHolderAllAndRefresh(Node[] selectors) {
        view.trackHolderPane.getChildren().addAll(selectors);
        view.trackHolderPane.setPrefHeight(selectors.length!=0 ? selectors[0].getScaleY()*selectors.length : 0);
    }

    final void clearSelectedHolder() {
        if (!view.trackHolderPane.getChildren().isEmpty()) {
            view.trackHolderPane.getChildren().clear();
        }
    }

    final String getPathFieldText() {
        return view.pathInput.getText();
    }

    private final List<Supplier<Boolean>> validators;

    final void addValidator(Supplier<Boolean> validator) {
        validators.add(validator);
    }

    final void enablePlayButtonWhenAllValidatorValid() {
        if (!view.trackHolderPane.getChildren().isEmpty() && !player.isAlive()) {
            view.playButton.setDisable(!validators.stream().allMatch(Supplier::get)); // validatorが全部OK -> trueを反転してfalse -> setDiable(false)なので最終的に有効になる setEnableも用意してくれ
        }
    }

    private boolean isTrackHolderElementSelectedEvenOne() {
        return view.trackHolderPane.getChildren().stream()
                    .filter(p -> p instanceof MFXToggleButton)
                    .map(m -> ((MFXToggleButton) m).selectedProperty())
                    .anyMatch(BooleanProperty::get);
    }

    final void setStopButtonDisable(boolean b) {
        view.stopButton.setDisable(b);
    }

    public final List<String> getPathFieldItem() {
        return view.pathInput.getItems();
    }

    public final void addItemIfNotContains(String newItem) {
        if (!view.pathInput.getItems().contains(newItem)) {
            view.pathInput.getItems().add(newItem);
        }
    }
}
