package com.kmidiplayer.application;

import javafx.application.Application;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.keylogger.KeyboardInput;

public class Main {

    private final static Main AP = new Main();
    private final KeyboardInput KBhook;
    private final Logger logger;

    private Main() {
        logger = LogManager.getLogger("[App]");
        ConfigHolder.instance().loadCommonSettings();
        KBhook = new KeyboardInput();
    }

    public static void main(String[] args) throws JsonProcessingException, IOException {
        if (args.length != 0) {
            ConfigHolder.instance().applyLaunchArgs(args);
        }
        Application.launch(UI.class);
    }

    public static Logger logger() {
        return AP.logger;
    }

    public static KeyboardInput getKeyInput() {
        return AP.KBhook;
    }
}