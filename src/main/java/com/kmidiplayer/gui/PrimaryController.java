package com.kmidiplayer.gui;

import java.io.File;
import java.util.List;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import com.kmidiplayer.application.UI;

public class PrimaryController {

    /*
     * JavaFx Drag&Drop
     * refernce(JP):
     *
     *  "Drag&Drop on JavaFx Sample" -senooken
     *   -> https://senooken.jp/post/2017/08/20/
     *
     *  "Drag&Drop" -Oracle
     *   -> https://docs.oracle.com/javase/jp/8/javafx/events-tutorial/drag-drop.htm
     *
     *  "Clipboard" -JavaDoc
     *   -> https://docs.oracle.com/javase/jp/8/javafx/api/javafx/scene/input/Clipboard.html
     *
     *  "List" -JavaDoc
     *   -> https://docs.oracle.com/javase/jp/8/docs/api/java/util/List.html
     *
     *  "List" -hydrocul.github.io
     *   -> https://hydrocul.github.io/wiki/programming_languages_diff/list/head.html
    */

    @FXML private Button runButton;
    @FXML private Button stopButton;
    @FXML private Rectangle dropField;
    @FXML private AnchorPane mainPane;
    @FXML private TextField delaySec;
    @FXML private CheckBox ckBoxTrackDivine;
    @FXML private MenuButton menuButtonSelectTrack;
    @FXML private Button convertButton;

    private final static PrimaryModel MODEL = PrimaryModel.getInstance();

    @FXML
        public void trackDivineChanged() {
            if (MODEL.hasFile()) {
                MODEL.loadFile(ckBoxTrackDivine.selectedProperty().get(), runButton, menuButtonSelectTrack, convertButton);
            }
            menuButtonSelectTrack.setDisable(ckBoxTrackDivine.selectedProperty().get() ? true : MODEL.hasLoadedData());
        }

    @FXML
        public void dragOver(DragEvent event){
            if (event.getGestureSource() != dropField &&
                    event.getDragboard().hasFiles()){
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        }

    @FXML
        public void dragEntered(DragEvent event){
            dropField.setFill(Color.rgb(0, 0, 0, 0.2));
        }

    @FXML
        public void dragExited(DragEvent event){
            dropField.setFill(Color.rgb(0, 0, 0, 0));
        }
    
    @FXML
        public void dragDropped(DragEvent event){
            // もし既に再生が始まっているようであれば上書きの用意のため停止し破棄
            MODEL.clearPlayer();
            // ドロップされたファイルをロード
            Dragboard db = event.getDragboard();
            final boolean HAS_DB_FILES = db.hasFiles();
            if (HAS_DB_FILES){
                List<File> dropped_File = db.getFiles();
                Objects.requireNonNull(dropped_File.get(0));
                UI.logger().info( "Loaded File Path: \"" + dropped_File.get(0).toString() + "\"" );

                ckBoxTrackDivine.setDisable(true);
                MODEL.setData(dropped_File.get(0), ckBoxTrackDivine.selectedProperty().get(), runButton, menuButtonSelectTrack, convertButton);
                ckBoxTrackDivine.setDisable(false);
            }
            event.setDropCompleted(HAS_DB_FILES);
            event.consume();
        }

    @FXML
        public void convertData() {
            runButton.setDisable(MODEL.convertData());
        }

    @FXML
        private void gorunButton() throws InterruptedException{
            stopButton.setDisable(!MODEL.startPlayer(ckBoxTrackDivine.selectedProperty().get(), Integer.parseInt(delaySec.getText())));
        }

    @FXML
        private void stopButton(){
            // 破棄
            MODEL.clearPlayer();
            // Gui起動時の状態にリセット
            runButton.setDisable(true);
            stopButton.setDisable(true);
            convertButton.setDisable(true);
            ckBoxTrackDivine.setDisable(false);
            menuButtonSelectTrack.setDisable(true);
        };
}
