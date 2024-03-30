package com.kmidiplayer;

import javafx.application.Application;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kmidiplayer.gui.Gui;
import com.kmidiplayer.keylogger.KeyboardInput;

public class App {

    private boolean debug;
    private final static App instance = new App();
    private final KeyboardInput keyboardhook;
    private final Logger logger;

    private App() {
        debug = true;
        logger = LogManager.getLogger();
        keyboardhook = new KeyboardInput();
    }

    // launch() => init() -> start() -> stop()
    public static void main(String[] args) throws JsonProcessingException, IOException {
        Application.launch(Gui.class);
    }

    public static void debugSetter(boolean bool){
        instance.debug = bool;
    }

    public static boolean debugGetter(){
        return instance.debug;
    }

    public static Logger logger() {
        return instance.logger;
    }

    public static KeyboardInput getKeyInput() {
        return instance.keyboardhook;
    }
}