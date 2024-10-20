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
        // エラー見る限りでは変換時にここでおかしくなっていて、ターミナルのエラーをbase64でデコードするとcssの中身らしきものが吐き出される
        // setGlobal() > Application.setUserAgentStylesheet() > PlatformImpl.setPlatformUserAgentStylesheet() > _setPlatformUserAgentStylesheet()
        // > StyleManager.getInstance().setUserAgentStylesheets() > _addUserAgentStylesheet() > loadStylesheet() > loadStylesheetUnPrivileged()
        // > getURL() > ???
        // getURLで死んでいるわけでは無いっぽい？もう分からん
        // とにかく、渡される内容がおかしいのか、バグり散らかしている

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
