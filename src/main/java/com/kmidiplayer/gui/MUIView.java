package com.kmidiplayer.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.util.Resource;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// TODO MaterialFxによるViewを作る
public class MUIView {

    private static final Logger LOGGER = LogManager.getLogger("[UIV]");

    private final MUIController controller;
    private final AnchorPane BASE;

    private final double HEIGHT = 384.0D;
    public double getHeight() { return HEIGHT; };
    private final double WIDTH  = 600.0D;
    public double getWidth() { return WIDTH; };

    private final String TITLE = "keystroke midifile player";
    public String getTitle() { return TITLE; };

    private final Image ICON;
    public Image getIcon() { return ICON; };

    public MUIView(Stage stage) {
        controller = new MUIController(this, stage);
        ICON = new Image(Resource.getFIleURLAsString(Main.class, "images", "icon.png"));
        BASE = new AnchorPane();
        BASE.setStyle("-fx-background-color: white");
            /*
             * <AnchorPane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="384.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1">
             *    <children>
             *       <Button layoutX="189.0" layoutY="344.0" mnemonicParsing="false" prefHeight="20.0" prefWidth="175.0" text="Stop" AnchorPane.bottomAnchor="15.0" AnchorPane.leftAnchor="190.0" />
             *       <Button layoutX="13.0" layoutY="269.0" mnemonicParsing="false" prefHeight="20.0" prefWidth="175.0" text="Play" AnchorPane.bottomAnchor="15.0" AnchorPane.leftAnchor="15.0" />
             *       <TextField layoutX="189.0" layoutY="310.0" prefHeight="25.0" prefWidth="175.0" promptText="delay (seconds)" AnchorPane.bottomAnchor="50.0" AnchorPane.leftAnchor="190.0" />
             *       <TextField layoutX="189.0" layoutY="274.0" prefHeight="25.0" prefWidth="175.0" promptText="window name" AnchorPane.bottomAnchor="85.0" AnchorPane.leftAnchor="190.0" />
             *       <CheckBox layoutX="15.0" layoutY="314.0" mnemonicParsing="false" text="use high-precision mode" AnchorPane.bottomAnchor="55.0" AnchorPane.leftAnchor="20.0" />
             *       <ScrollPane layoutX="386.0" layoutY="37.0" prefHeight="329.0" prefWidth="200.0" AnchorPane.bottomAnchor="15.0" AnchorPane.rightAnchor="15.0" AnchorPane.topAnchor="40.0">
             *          <content>
             *             <VBox prefHeight="400.0" prefWidth="185.0" />
             *          </content>
             *       </ScrollPane>
             *       <Label layoutX="386.0" layoutY="19.0" text="tracks" AnchorPane.rightAnchor="180.0" AnchorPane.topAnchor="20.0" />
             *    </children>
             * </AnchorPane>
             * 
            */
            final Text dropText1 = new Text("↑");
             dropText1.setLayoutX(169.0D);
             dropText1.setLayoutY(106.0D);
            final Text dropText2 = new Text("Drag & Drop here");
             dropText2.setLayoutX(127.0D);
             dropText2.setLayoutY(123.0D);
            final AnchorPane fileDropArea = new AnchorPane(dropText1, dropText2);
             fileDropArea.setPrefHeight(220.0D);
             fileDropArea.setPrefWidth(350.0D);
             fileDropArea.setLayoutX(14.0D);
             fileDropArea.setLayoutY(42.0D);
             fileDropArea.setStyle("-fx-background-color: #D3D3D3;");
            final TextField midPathField = new TextField();
             midPathField.setLayoutX(14.0D);
             midPathField.setLayoutY(17.0D);
             midPathField.setPromptText("path");
             midPathField.setPrefHeight(25.0D);
             midPathField.setPrefWidth(285.0D);
            final Button pathReset = new Button();
             pathReset.setLayoutX(300.0D);
             pathReset.setLayoutY(17.0D);
             pathReset.setPrefHeight(25.0D);
             pathReset.setPrefWidth(65.0D);
             pathReset.setText("reset");

        BASE.getChildren().addAll(fileDropArea, midPathField, pathReset);
    }

    public Pane getBasePane() {
        return BASE;
    }

}
