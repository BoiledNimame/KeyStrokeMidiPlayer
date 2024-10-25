package com.kmidiplayer.application;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.keylogger.IInputter;
import com.kmidiplayer.keylogger.KeyboardInput;
import com.kmidiplayer.keylogger.KeyboardMock;

public class Main {

    private static Main AP;
    private final IInputter KBhook;
    private static final Logger LOGGER = LogManager.getLogger("[App]");

    private Main(boolean isMock) {
        if (isMock) {
            LOGGER.info("Running as mock mode");
            KBhook = new KeyboardMock();
        } else {
            LOGGER.info("Running as normal mode");
            KBhook = new KeyboardInput();
        }
    }

    public static void main(String[] args) throws IOException {
        final List<String> arglist = Arrays.asList(args);

        AP = new Main(arglist.contains("-mock"));

        LOGGER.info("Launch material design UI");
        Application.launch(MUI.class);
    }

    public static Logger logger() {
        return LOGGER;
    }

    public static IInputter getKeyInput() {
        return AP.KBhook;
    }
}