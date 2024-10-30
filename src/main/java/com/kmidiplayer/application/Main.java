package com.kmidiplayer.application;

import java.util.Arrays;
import java.util.List;

import com.kmidiplayer.config.ConfigHolder;
import javafx.application.Application;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.keylogger.IInputter;
import com.kmidiplayer.keylogger.KeyboardInput;
import com.kmidiplayer.keylogger.KeyboardMock;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger("[App]");

    private Main(boolean isMock) {}

    public static void main(String[] args) throws IOException {
        final List<String> arglist = Arrays.asList(args);

        final boolean isMock = arglist.contains("-mock");

        ConfigHolder.configs.setMockMode(isMock);

        if (isMock) {
            LOGGER.info("Running as mock mode");
        } else {
            LOGGER.info("Running as normal mode");
        }

        LOGGER.info("Launch material design UI");
        Application.launch(MUI.class);
    }
}