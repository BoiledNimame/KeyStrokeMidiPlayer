package com.kmidiplayer.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.Options;
import com.kmidiplayer.gui.MUIView;
import com.kmidiplayer.util.ResourceLocation;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MUI extends Application {

    private final static Logger LOGGER = LogManager.getLogger("[UI]");

    @Override
    public void init() {

    }

    @Override
    public void start(Stage stage) throws IOException {

        final long begin = System.nanoTime();

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

        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(VIEW.getIcon());
        stage.setTitle(VIEW.getTitle());
        stage.setResizable(false);
        stage.setScene(new Scene(VIEW.getRootPane(), VIEW.getWindowWidth(), VIEW.getWindowHeight()));
        stage.show();

        if (Options.configs.useNoteUI()) {
            VIEW.showKeyInputPreviewUIView();
        }

        final long end = System.nanoTime();

        LOGGER.debug("Up to screen display: ".concat(Long.toString((end - begin) / 1000000)).concat("ms"));

    }

    @Override
    public void stop() {
        Platform.exit(); // 完全に終了させる...
    }

    public static Logger logger() {
        return LOGGER;
    }
}
