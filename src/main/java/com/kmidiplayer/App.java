package com.kmidiplayer;

import javafx.application.Application;
import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kmidiplayer.gui.Gui;
import com.kmidiplayer.keylogger.KeyboardInput;

public class App {

    private static boolean debug = true;
    private static final Logger logger = Logger.getLogger("MAIN");

    // launch() => init() -> start() -> stop()
    public static void main(String[] args) throws JsonProcessingException, IOException {
        KeyboardInput.KeyboardInputInitialization();
        Application.launch(Gui.class);
    }

    public static void debugSetter(boolean bool){
        debug = bool;
    }

    public static boolean debugGetter(){
        return debug;
    }

    public static Logger logger() {
        return logger;
    }
}