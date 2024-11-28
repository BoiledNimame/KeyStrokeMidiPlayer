package com.kmidiplayer.gui;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kmidiplayer.config.Options;
import com.kmidiplayer.midi.event.NoteEvent;
import com.kmidiplayer.util.Pair;
import com.kmidiplayer.util.ResourceLocation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * NoteNumberOffsetの調整をやりやすくするための鍵盤ぽいサブウィンドウのViewクラス
 */
public class NoteUIView {

    private final Pane root;
    private final MUIView parentView;

    private static final String[] NOTE_NAMES = new String[]{
        " ", "#", " ", "#", " ", " ", "#", " ", "#", " ", // 0~9
        "#", " ", " ", "#", " ", "#", " ", " ", "#", " ", // 10~19
        "#", "A0", "A#0", "B0", "C1", "C#1", "D1", "D#1", "E1", "F1", // 20~29
        "F#1", "G1", "G#1", "A1", "A#1", "B1", "C2", "C#2", "D2", "D#2", // 30~39
        "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2", "C3", "C#3", // 40~49
        "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", // 50~59
        "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", // 60~69
        "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", // 70~79
        "G#5", "A5", "A#5", "B5", "C6", "C#6", "D6", "D#6", "E6", "F6", // 80~89
        "F#6", "G6", "G#6", "A6", "A#6", "B6", "C7", "C#7", "D7", "D#7", // 90~99
        "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7", "C8", "C#8", // 100~109
        "D8", "D#8", "E8", "F8", "F#8", "G8", "G#8", "A8", "A#8", "B8", // 110~119
        "C9", "C#9", "D9", "D#9", "E9", "F9", "F#9", "G9" // 120~127
    };

    private final List<Pair<String, KeyboardRegion>> keyBoardsRegion;

    private static final String KEY_WHITE_ID = "Region_Prev_KeyWhite";
    private static final String KEY_BLACK_ID = "Region_Prev_KeyBlack";

    public NoteUIView(MUIView view) {

        parentView = view;

        root = new AnchorPane();

        final int KEYBOARD_WIDTH = 22;
        final int KEYBOARD_HEIGHT = 60;

        keyBoardsRegion = Stream.of(NOTE_NAMES)
                                .map(s -> new Pair<>(s, new KeyboardRegion()))
                                .peek(a -> a.getValue().setId(a.getKey().contains("#") ? KEY_BLACK_ID : KEY_WHITE_ID))
                                .peek(a -> a.getValue().setPrefWidth(a.getKey().contains("#") ? KEYBOARD_WIDTH * 0.5D : KEYBOARD_WIDTH))
                                .peek(a -> a.getValue().setPrefHeight(a.getKey().contains("#") ? KEYBOARD_HEIGHT * 0.5D : KEYBOARD_HEIGHT))
                                .collect(Collectors.toList());

        // ピアノ鍵盤の並びになるようにroot上での位置を決める
        for (int i = 0; i < keyBoardsRegion.size(); i++) {
            AnchorPane.setTopAnchor(keyBoardsRegion.get(i).getValue(), 0D);
            if (i==0) {
                AnchorPane.setLeftAnchor(keyBoardsRegion.get(i).getValue(), 0D);
            } else {
                if (keyBoardsRegion.get(i).getKey().contains("#")) {
                    // 黒鍵盤
                    AnchorPane.setLeftAnchor(keyBoardsRegion.get(i).getValue(), AnchorPane.getLeftAnchor(keyBoardsRegion.get(i-1).getValue()) + (KEYBOARD_WIDTH / 1.33D));
                } else {
                    // 白鍵盤
                    if (keyBoardsRegion.get(i-1).getKey().contains("#")) { // 前が黒鍵盤かどうかで詰めるか決めてる
                        AnchorPane.setLeftAnchor(keyBoardsRegion.get(i).getValue(), AnchorPane.getLeftAnchor(keyBoardsRegion.get(i-2).getValue()) + KEYBOARD_WIDTH);
                    } else {
                        AnchorPane.setLeftAnchor(keyBoardsRegion.get(i).getValue(), AnchorPane.getLeftAnchor(keyBoardsRegion.get(i-1).getValue()) + KEYBOARD_WIDTH);
                    }
                }
            }
        }

        // レイヤの問題で白鍵盤を全て加えてから黒を加える
        root.setPrefSize(KEYBOARD_WIDTH * Stream.of(NOTE_NAMES).filter(s -> !s.contains("#")).count(), KEYBOARD_HEIGHT);
        root.getChildren().addAll(keyBoardsRegion.stream().filter(m -> !m.getKey().contains("#")).map(Pair::getValue).collect(Collectors.toList()));
        root.getChildren().addAll(keyBoardsRegion.stream().filter(m -> m.getKey().contains("#")).map(Pair::getValue).collect(Collectors.toList()));

        parentView.getcontroller().getModel().addBeforePlay(this::beforePlay);
        parentView.getcontroller().getModel().addAfterPlay(this::afterPlay);

        root.getStylesheets().add(ResourceLocation.CSS_CUSTOM.toURL().toExternalForm());
    }

    void setOffsetInfo(List<Pair<String, KeyboardRegion>> r) {
        for (int i = 0; i < r.size(); i++) {
            if (!definedNotes.contains(i)) {
                r.get(i).getValue().pseudoClassStateChanged(outRanged, true);
            }
        }
    }

    void beforePlay() {
        parentView.getcontroller().getModel().getPlayerSupplier().get().addEventListener(this::fired);
        noteNumberOffsetCache = Integer.parseInt(parentView.noteNumberOffsetInput.getText());
        setOffsetInfo(keyBoardsRegion);
    }

    void afterPlay() {
        keyBoardsRegion.forEach(this::setDefalutRelease);
    }

    private void setDefalutRelease(Pair<String, KeyboardRegion> nodePair) {
        // nodePair.getValue().pseudoClassStateChanged(outRangedPressed, false);
        // nodePair.getValue().pseudoClassStateChanged(outRanged, false);
        nodePair.getValue().outRangedProperty().set(false);
        // nodePair.getValue().pseudoClassStateChanged(pressed, false);
        nodePair.getValue().keyPressedProperty().set(false);
    }

    private final List<Integer> definedNotes = Options.configs.getKeyMap().keySet().stream().map(Integer::valueOf).collect(Collectors.toList());

    private int noteNumberOffsetCache;

    void fired(NoteEvent e) {

        final int buffedNoteNumber = e.getNoteNumber() + noteNumberOffsetCache;

        // 範囲外ならなにもしない(Index out of boundsが出るので...)
        if (buffedNoteNumber < 0 || keyBoardsRegion.size() <= buffedNoteNumber) {
            return;
        }


        // *Evil Css*
        keyBoardsRegion.get(buffedNoteNumber).getValue().pseudoClassStateChanged(outRanged, definedNotes.contains(buffedNoteNumber));
        // node.pseudoClassStateChanged(definedNotes.contains(buffedNoteNumber) ? pressed : outRangedPressed, e.isPushed());

        keyBoardsRegion.get(buffedNoteNumber).getValue().keyPressedProperty().set(e.isPushed());

    }

    public Pane getRoot() {
        return root;
    }

    // FIXME 謎を解明:: どうやら新たにBooleanPropertyを作成しそこから呼ばなければならない？らしい https://openjfx.io/javadoc/12/javafx.graphics/javafx/css/PseudoClass.html
    private static final PseudoClass pressed = PseudoClass.getPseudoClass("pressed");
    private static final PseudoClass outRanged = PseudoClass.getPseudoClass("outranged");
    private static final PseudoClass outRangedPressed = PseudoClass.getPseudoClass("outrangedpressed");

    private static final class KeyboardRegion extends Region {

        private final BooleanProperty keyPressed = new BooleanPropertyBase(false) {

            @Override protected void invalidated() {
                pseudoClassStateChanged(outRanged.get() ? outRangedPressed : pressed, get());
            }

            @Override
            public Object getBean() {
                return KeyboardRegion.this;
            }

            @Override
            public String getName() {
                return pressed.toString();
            }

        };

        private final BooleanProperty outRanged = new BooleanPropertyBase() {

            @Override protected void invalidated() {
                pseudoClassStateChanged(NoteUIView.outRanged, get());
            }

            @Override
            public Object getBean() {
                return KeyboardRegion.this;
            }

            @Override
            public String getName() {
                return outRanged.toString();
            }

        };

        private BooleanProperty keyPressedProperty() {
            return keyPressed;
        }

        private BooleanProperty outRangedProperty() {
            return outRanged;
        }

    }

}
