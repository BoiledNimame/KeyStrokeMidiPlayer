package com.kmidiplayer.application;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.util.Resource;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MUI extends Application {

    private static Scene scene;
    private final static Logger logger = LogManager.getLogger("[UI]");

    private static Stage sStage;
    public static Stage getStage() { return sStage; }

    private final double height = 156;
    private final double width  = 214;

    @Override
    public void init() throws InterruptedException, ExecutionException {
        // TODO sceneに乗せるUIを作る
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(new AnchorPane(), width, height);
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
