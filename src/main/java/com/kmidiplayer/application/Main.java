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
    private static final ConfigHolder HOLDER = ConfigHolder.instance();
    private static final Logger LOGGER = LogManager.getLogger("[App]");

    private Main() {
        HOLDER.loadCommonSettings();
        if (HOLDER.isMockMode()) {
            LOGGER.info("Running as mock mode");
            KBhook = new KeyboardMock();
        } else {
            LOGGER.info("Running as normal mode");
            KBhook = new KeyboardInput();
        }
    }

    public static void main(String[] args) throws JsonProcessingException, IOException {
        if (args.length != 0) {
            HOLDER.applyLaunchArgs(args);
        }
        AP = new Main();
        if (HOLDER.useFxml()) {
            LOGGER.info("Launch normal UI");
            Application.launch(UI.class);
        } else {
            LOGGER.info("Launch material design UI");
            Application.launch(MUI.class);
        }
    }

    public static Logger logger() {
        return LOGGER;
    }

    public static IInputter getKeyInput() {
        return AP.KBhook;
    }
}