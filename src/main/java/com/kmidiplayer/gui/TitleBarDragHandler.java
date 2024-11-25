package com.kmidiplayer.gui;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * 非常に不服
 * 参考: https://gist.github.com/jewelsea/2658491
 */
public class TitleBarDragHandler {

    private final Stage targetStage;

    private double posOffsetX;
    private double posOffsetY;

    private TitleBarDragHandler(Stage stage) {
        targetStage = stage;
    }

    void titleBar_onMousePressed(MouseEvent event) {
        posOffsetX = targetStage.getX() - event.getScreenX();
        posOffsetY = targetStage.getY() - event.getScreenY();
    }

    void titleBar_onMouseDragged(MouseEvent event) {
        targetStage.setX(event.getScreenX() + posOffsetX);
        targetStage.setY(event.getScreenY() + posOffsetY);
    }

    static TitleBarDragHandler buildHandlers(Stage stage) {
        return new TitleBarDragHandler(stage);
    }

}
