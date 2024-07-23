package com.kmidiplayer.application;

import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.util.Resource;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class UI extends Application {

    private static Scene scene;
    private final static Logger logger = LogManager.getLogger("[UI]");

    @Override
    public void start(Stage stage) throws IOException {
        //primaryを呼び出しsizを固定 正直いらない
        scene = new Scene(loadFXML("primary"), 214, 156);
        Image imageIcon = new Image(Resource.getFileURL(Main.class, "images", "icon.png").toString());
        stage.getIcons().add(imageIcon);
        stage.setTitle("keystroke midifile player");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        final String filename = fxml.endsWith(".fxml") ? fxml : fxml + ".fxml";
        final URL fxmlLocation = Resource.getFileURL(Main.class, "fxml", filename);
        logger.info("Loading Fxml from url: " + fxmlLocation);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        return fxmlLoader.load();
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static Logger logger() {
        return logger;
    }
}
