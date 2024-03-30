package com.kmidiplayer.gui;

import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Gui extends Application {

    private static Scene scene;
    private final static Logger logger = LogManager.getLogger("Gui");

    @Override
    public void start(Stage stage) throws IOException {
        //primaryを呼び出しsizを固定 正直いらない
        scene = new Scene(loadFXML("primary"), 214, 156);
        Image imageIcon = new Image("file:src\\main\\resources\\com\\kmidiplayer\\icon.png");
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
        final URL fxmlLocation = App.class.getResource(fxml + ".fxml");
        logger.info("Loading Fxml from url: " + fxmlLocation);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        return fxmlLoader.load();
    }

    public static Logger logger() {
        return logger;
    }
}
