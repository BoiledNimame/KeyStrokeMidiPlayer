package com.kmidiplayer.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.application.Main;
import com.kmidiplayer.util.Resource;

import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
             *       <AnchorPane layoutX="14.0" layoutY="42.0" prefHeight="220.0" prefWidth="350.0" style="-fx-background-color: #D3D3D3;" AnchorPane.leftAnchor="15.0" AnchorPane.topAnchor="40.0">
             *          <children>
             *             <Text layoutX="127.0" layoutY="123.0" strokeType="OUTSIDE" strokeWidth="0.0" text="Drag &amp; Drop here" />
             *             <Text layoutX="169.0" layoutY="106.0" strokeType="OUTSIDE" strokeWidth="0.0" text="↑" />
             *          </children>
             *       </AnchorPane>
             *       <TextField layoutX="14.0" layoutY="17.0" prefHeight="25.0" prefWidth="285.0" promptText="path" AnchorPane.leftAnchor="15.0" AnchorPane.topAnchor="15.0" />
             *       <Button layoutX="300.0" layoutY="17.0" mnemonicParsing="false" prefHeight="25.0" prefWidth="65.0" text="reset" AnchorPane.leftAnchor="300.0" AnchorPane.topAnchor="15.0" />
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
    }

    public Pane getBasePane() {
        return BASE;
    }

}
