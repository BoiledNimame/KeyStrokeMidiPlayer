package com.kmidiplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kmidiplayer.keylogger.KeyboardInput;

public class App extends Application {

    private static Scene scene;
    private static boolean debug = true;

    // launch() => init() -> start() -> stop()
    public static void main(String[] args) throws JsonProcessingException, IOException {
        KeyboardInput.KeyboardInputInitialization();
        launch();
    }

    public static void debugSetter(boolean bool){
        debug = bool;
    }

    public static boolean debugGetter(){
        return debug;
    }

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
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}