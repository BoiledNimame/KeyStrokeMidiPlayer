package com.kmidiplayer;

import javafx.application.Application;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.gui.Gui;
import com.kmidiplayer.keylogger.KeyboardInput;

public class App {

    private final static App AP = new App();
    private final KeyboardInput KBhook;
    private final Logger logger;

    private App() {
        logger = LogManager.getLogger("[App]");
        ConfigHolder.instance().loadCommonSettings();
        KBhook = new KeyboardInput();
    }

    public static void main(String[] args) throws JsonProcessingException, IOException {
        Application.launch(Gui.class);
    }

    public static Logger logger() {
        return AP.logger;
    }

    public static KeyboardInput getKeyInput() {
        return AP.KBhook;
    }
}