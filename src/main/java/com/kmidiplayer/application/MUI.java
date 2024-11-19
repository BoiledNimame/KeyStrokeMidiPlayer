package com.kmidiplayer.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.Options;
import com.kmidiplayer.gui.MUIView;
import com.kmidiplayer.gui.NoteUIView;
import com.kmidiplayer.util.ResourceLocation;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MUI extends Application {

    private final static Logger logger = LogManager.getLogger("[UI]");

    @Override
    public void init() {

    }

    @Override
    public void start(Stage stage) throws IOException {

        final File styleSheetFile = ResourceLocation.CSS_DEFAULT.toFile();

        if (!styleSheetFile.exists()) {
            Files.write(
                styleSheetFile.toPath(),
                UserAgentBuilder.builder()
                    .themes(JavaFXThemes.MODENA)
                    .themes(MaterialFXStylesheets.forAssemble(true))
                    .setDeploy(true)
                    .setResolveAssets(true)
                    .build().toString().getBytes()
            );
        }

        final MUIView VIEW = new MUIView(stage, styleSheetFile.toPath().toUri().toURL().toExternalForm());

        stage.getIcons().add(VIEW.getIcon());
        stage.setTitle(VIEW.getTitle());
        stage.setResizable(false);
        stage.setScene(new Scene(VIEW.getRootPane(), VIEW.getWidth(), VIEW.getHeight()));
        stage.show();

        if (Options.configs.useNoteUI()) {

            final NoteUIView nView = new NoteUIView(VIEW);
            final Stage nStage = new Stage();
            final Scene nScene = new Scene(nView.getRoot(), nView.getRoot().getPrefWidth(), nView.getRoot().getPrefHeight());
            nStage.setScene(nScene);
            nStage.setResizable(false);
            nStage.initStyle(StageStyle.UTILITY);
            nStage.initOwner(stage);

            nStage.show();
            nStage.setX(nStage.getOwner().getX() - (nStage.getWidth() / 2) + (nStage.getOwner().getWidth() / 2));
            nStage.setY(nStage.getOwner().getY() + nStage.getOwner().getHeight());
        }
    }

    @Override
    public void stop() {

    }

    public static Logger logger() {
        return logger;
    }
}
