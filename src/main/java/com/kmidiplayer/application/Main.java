package com.kmidiplayer.application;

import javafx.application.Application;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.keylogger.IInputter;
import com.kmidiplayer.keylogger.KeyboardInput;
import com.kmidiplayer.keylogger.KeyboardMock;

public class Main {

    private static Main AP;
    private final IInputter KBhook;
    private static final Logger logger = LogManager.getLogger("[App]");

    private Main() {
        ConfigHolder.instance().loadCommonSettings();
        if (ConfigHolder.instance().isMockMode()) {
            KBhook = new KeyboardMock();
            logger.info("Running as mock mode");
        } else {
            KBhook = new KeyboardInput();
            logger.info("Running as normal mode");
        }
    }

    public static void main(String[] args) throws JsonProcessingException, IOException {
        if (args.length != 0) {
            ConfigHolder.instance().applyLaunchArgs(args);
        }
        AP = new Main();
        Application.launch(UI.class);
    }

    public static Logger logger() {
        return logger;
    }

    public static IInputter getKeyInput() {
        return AP.KBhook;
    }
}