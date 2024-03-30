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
    private final static App AP = new App();
    private final KeyboardInput KBhook;
    private final Logger logger;

    private App() {
        debug = true;
        logger = LogManager.getLogger("[App]");
        KBhook = new KeyboardInput();
    }

    public static void main(String[] args) throws JsonProcessingException, IOException {
        AP.debug = AP.KBhook.isDebug();
        Application.launch(Gui.class);
    }

    public static boolean isDebugMode(){
        return AP.debug;
    }

    public static Logger logger() {
        return AP.logger;
    }

    public static KeyboardInput getKeyInput() {
        return AP.KBhook;
    }
}