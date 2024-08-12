package com.kmidiplayer.application;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.gui.MUIView;
import com.kmidiplayer.util.Resource;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MUI extends Application {

    private static Scene scene;
    private final static Logger logger = LogManager.getLogger("[UI]");

    private static Stage sStage;
    public static Stage getStage() { return sStage; }

    private final MUIView VIEW = new MUIView();
    private final double HEIGHT = 156;
    private final double WIDTH  = 214;

    @Override
    public void init() {

    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(VIEW.getBasePane(), WIDTH, HEIGHT);
        Image imageIcon = new Image(Resource.getFIleURLAsString(Main.class, "images", "icon.png"));
        stage.getIcons().add(imageIcon);
        stage.setTitle("keystroke midifile player");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        sStage = stage;

        // TODO 外側から登録する形にするべき
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) { termination(); }
        });
    }

    @Override
    public void stop() {
        termination();
    }

    private void termination() {
        // ウィンドウが閉じた直後に行われる終了処理
    }

    public static Logger logger() {
        return logger;
    }
}
