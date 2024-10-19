package com.kmidiplayer.application;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.gui.MUIView;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MUI extends Application {

    private final static Logger logger = LogManager.getLogger("[UI]");

    @Override
    public void init() {

    }

    @Override
    public void start(Stage stage) throws IOException {
        UserAgentBuilder.builder()
            .themes(JavaFXThemes.MODENA)
            .themes(MaterialFXStylesheets.forAssemble(true))
            .setDeploy(true)
            .setResolveAssets(true)
            .build()
            .setGlobal();

        final MUIView VIEW = new MUIView(stage);

        stage.getIcons().add(VIEW.getIcon());
        stage.setTitle(VIEW.getTitle());
        stage.setResizable(false);
        stage.setScene(new Scene(VIEW.getBasePane(), VIEW.getWidth(), VIEW.getHeight()));
        stage.show();
    }

    @Override
    public void stop() {

    }

    public static Logger logger() {
        return logger;
    }
}
