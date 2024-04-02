package com.kmidiplayer.gui;

import java.io.File;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import com.kmidiplayer.App;
import com.kmidiplayer.midi.midiData;
import com.kmidiplayer.midi.midiPlayer;

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
    private static boolean isFileLoadSucsess = false;
    @FXML private AnchorPane mainPane;
    @FXML private TextField delaySec;
    @FXML private TextField textOutput;
    
    public static void IsFileLoadSucsessSetter(boolean bool) {
        isFileLoadSucsess = bool;
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
    
    private midiData midiData = null;
    private midiPlayer player = null;
    @FXML
        public void dragDropped(DragEvent event){
            Dragboard db = event.getDragboard();
            final boolean HAS_DB_FILES = db.hasFiles();
            if (HAS_DB_FILES){
                List<File> dropped_File = db.getFiles();
                Gui.logger().info( "Loaded File Path: \"" + dropped_File.get(0).toString() + "\"" );
                midiData = new midiData(dropped_File.get(0).toString());
                if(isFileLoadSucsess ==true){
                    runButton.setDisable(false);
                }
            }
            event.setDropCompleted(HAS_DB_FILES);
            event.consume();
        }

    @FXML
        private void gorunButton() throws InterruptedException{
            // 再生遅延
            try{
                Thread.sleep(Integer.parseInt(delaySec.getText())*1000);
            } catch (NumberFormatException e) {
                textOutput.setText("遅延の入力欄に数値以外が入力されています");
                e.printStackTrace();
            }
            // 別スレッドで再生開始
            if (midiData != null) {
                player = new midiPlayer(App.getKeyInput(), midiData, midiData.getTickInMilliSeconds());
                player.start();
                stopButton.setDisable(false);
            } else {
                Gui.logger().error("The midi file has not been converted correctly or is not working properly.");
            }
        }

    @FXML
        private void stopButton(){
            if (player != null) {
                // 再生しているスレッドを停止させる
                player.interrupt();
                // スレッドを破棄する
                player = null;
                runButton.setDisable(true);
            }
            stopButton.setDisable(true);
        };
}
