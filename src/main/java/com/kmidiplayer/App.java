package com.kmidiplayer;

import javafx.application.Application;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kmidiplayer.gui.Gui;
import com.kmidiplayer.keylogger.KeyboardInput;

public class App {

    private static boolean debug = true;
    private final static Logger logger = LogManager.getLogger();

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