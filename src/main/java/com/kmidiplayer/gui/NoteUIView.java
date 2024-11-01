package com.kmidiplayer.gui;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kmidiplayer.util.Pair;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class NoteUIView {
    private final Pane ROOT;

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

    final List<Pair<String, Region>> kboards;

    public NoteUIView() {
        ROOT = new AnchorPane();

        final int WIDTH = 22;
        final int HEIGHT = 60;

        kboards = Stream.of(NOTE_NAMES)
                        .map(s -> new Pair<>(s, new Region()))
                        .peek(a -> a.getValue().setStyle(
                           "-fx-background-color: "
                               .concat(a.getTag().contains("#") ? "black" : "white")
                               .concat("; -fx-border-style: solid; -fx-border-width: 0.5; -fx-border-color: black;")
                        ))
                        .peek(a -> a.getValue().setPrefWidth(a.getTag().contains("#") ? WIDTH * 0.5D : WIDTH))
                        .peek(a -> a.getValue().setPrefHeight(a.getTag().contains("#") ? HEIGHT * 0.5D : HEIGHT))
                        .collect(Collectors.toList());

        // レイアウト
        for (int i = 0; i < kboards.size(); i++) {
            AnchorPane.setTopAnchor(kboards.get(i).getValue(), 0D);
            if (i==0) {
                AnchorPane.setLeftAnchor(kboards.get(i).getValue(), 0D);
            } else {
                if (kboards.get(i).getTag().contains("#")) {
                    // 黒鍵盤
                    AnchorPane.setLeftAnchor(kboards.get(i).getValue(), AnchorPane.getLeftAnchor(kboards.get(i-1).getValue()) + (WIDTH / 1.33D));
                } else {
                    // 白鍵盤
                    if (kboards.get(i-1).getTag().contains("#")) {
                        AnchorPane.setLeftAnchor(kboards.get(i).getValue(), AnchorPane.getLeftAnchor(kboards.get(i-2).getValue()) + WIDTH);
                    } else {
                        AnchorPane.setLeftAnchor(kboards.get(i).getValue(), AnchorPane.getLeftAnchor(kboards.get(i-1).getValue()) + WIDTH);
                    }
                }
            }
        }

        ROOT.setPrefSize(WIDTH * Stream.of(NOTE_NAMES).filter(s -> !s.contains("#")).count(), HEIGHT);
        ROOT.getChildren().addAll(kboards.stream().filter(m -> !m.getTag().contains("#")).map(Pair::getValue).collect(Collectors.toList()));
        ROOT.getChildren().addAll(kboards.stream().filter(m -> m.getTag().contains("#")).map(Pair::getValue).collect(Collectors.toList()));
    }

    List<Pair<String, Region>> getKeyBoards() {
        return kboards;
    }

    public Pane getRoot() {
        return ROOT;
    }
}
