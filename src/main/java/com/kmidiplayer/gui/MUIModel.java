package com.kmidiplayer.gui;

public class MUIModel {
    private final MUIView view;

    MUIModel(MUIView view) {
        this.view = view;
    }

    public void setPath(String text) {
        if ("".equals(text) || text==null) {
            view.getPathField().clear();
        } else {
            view.getPathField().setText(text);
        }
    }

}
