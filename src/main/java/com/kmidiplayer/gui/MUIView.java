package com.kmidiplayer.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.util.Resource;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

// TODO MaterialFxによるViewを作る
public class MUIView {

    private static final Logger LOGGER = LogManager.getLogger("[UIV]");

    private final MUIController controller;
    private final BorderPane BASE;

    private final double HEIGHT = 156;
    public double getHeight() { return HEIGHT; };
    private final double WIDTH  = 214;
    public double getWidth() { return WIDTH; };

    private final String TITLE = "keystroke midifile player";
    public String getTitle() { return TITLE; };

    private final Image ICON;
    public Image getIcon() { return ICON; };

    public MUIView(Stage stage) {

        controller = new MUIController(this, stage);
        ICON = new Image(Resource.getFIleURLAsString(Main.class, "images", "icon.png"));
        BASE = new BorderPane();
        BASE.setStyle("-fx-background-color: white");

            final VBox VBOX = new VBox();
            VBOX.setStyle("-fx-background-color: white");

                final Button jButton = new Button("Normal");
                jButton.setMaxWidth(Double.MAX_VALUE);
                jButton.setId("jButton");
                jButton.getStylesheets().add(MUIView.class.getResource("View.css").toExternalForm());
                jButton.setOnAction((e) -> {LOGGER.info(e.getSource().toString() + "Clicked!");});

                final MFXButton mButton = new MFXButton("MaterialFx");
                mButton.setMaxWidth(Double.MAX_VALUE);
                mButton.setId("mButton");
                mButton.getStylesheets().add(MUIView.class.getResource("View.css").toExternalForm());
                mButton.setOnAction((e) -> {LOGGER.info(e.getSource().toString() + "Clicked!");});
                // 結局cssじゃねえかよ
                // https://github.com/palexdev/MaterialFX/blob/main/demo/src/main/resources/io/github/palexdev/materialfx/demo/css/Buttons.css

                final MFXButton mButton2 = new MFXButton();
                mButton2.setMaxWidth(Double.MAX_VALUE);
                mButton2.setId("mButton2");
                mButton2.getStylesheets().add(MUIView.class.getResource("View.css").toExternalForm());

                // ファイルをドラッグ&ドロップするエリア (クリックしたらファイル選択の画面が出るとよし)

                // トラック選択のMenuButton

                // 再生するボタン

                // 停止するボタン

                // 再生遅延の入力フィールド

                // 入力先ウィンドウ名の入力フィールド

                // 高精度モードの切り替えチェックボックス

            VBOX.setSpacing(10);
            VBOX.getChildren().addAll(jButton, mButton, mButton2);

        BASE.setPadding(new Insets(20, 20, 20, 20));
        BASE.setCenter(VBOX);
    }

    public Pane getBasePane() {
        return BASE;
    }

}
