package com.kmidiplayer.application;

import java.util.Arrays;
import java.util.List;

import com.kmidiplayer.config.ConfigHolder;
import javafx.application.Application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger("[App]");

    private Main() {}

    public static void main(String[] args) {
        final List<String> arglist = Arrays.asList(args);

        final boolean isMock = arglist.contains("-mock");

        ConfigHolder.configs.setMockMode(isMock);

        LOGGER.info(isMock ? "Running as mock mode" : "Running as normal mode");

        Application.launch(MUI.class);
    }
}