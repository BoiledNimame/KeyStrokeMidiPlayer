package com.kmidiplayer.gui;

import java.io.File;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import com.kmidiplayer.App;
import com.kmidiplayer.midi.integrated.MidiData;
import com.kmidiplayer.midi.integrated.MidiPlayer;
import com.kmidiplayer.midi.multi.MultiTrackMidiData;
import com.kmidiplayer.midi.multi.MultiTrackMidiLoader;
import com.kmidiplayer.midi.multi.MultiTrackMidiPlayer;

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
    @FXML private CheckBox ckBoxTrackDivine;
    @FXML private MenuButton menuButtonSelectTrack;
    @FXML private Button convertButton;
    
    public static void IsFileLoadSucsessSetter(boolean bool) {
        isFileLoadSucsess = bool;
    }

    @FXML
        public void trackDivineChanged() {
            menuButtonSelectTrack.setDisable(ckBoxTrackDivine.selectedProperty().get());
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
    
    private MidiData midiData = null;
    private MidiPlayer player = null;
    private MultiTrackMidiData mMidiData = null;
    private MultiTrackMidiPlayer mPlayer = null;
    @FXML
        public void dragDropped(DragEvent event){
            // もし既に再生が始まっているようであれば上書きの用意のため停止し破棄
            clearPlayer();
            // ドロップされたファイルをロード
            Dragboard db = event.getDragboard();
            final boolean HAS_DB_FILES = db.hasFiles();
            if (HAS_DB_FILES){
                List<File> dropped_File = db.getFiles();
                Gui.logger().info( "Loaded File Path: \"" + dropped_File.get(0).toString() + "\"" );

                ckBoxTrackDivine.setDisable(true);
                if (ckBoxTrackDivine.selectedProperty().get()) {
                    midiData = new MidiData(dropped_File.get(0));
                    if(isFileLoadSucsess ==true){
                        runButton.setDisable(false);
                    }
                } else {
                    menuButtonSelectTrack.setDisable(false);
                    mMidiData = MultiTrackMidiLoader.loadFileToDataObject(dropped_File.get(0));
                    if (!menuButtonSelectTrack.getItems().isEmpty()) {
                        menuButtonSelectTrack.getItems().clear();
                    }
                    for (int i = 0; i < mMidiData.getTrackInfo().length; i++) {
                        menuButtonSelectTrack.getItems().add(new MenuItem(mMidiData.getTrackInfo()[i]));
                        final int currentLoopNumber = i;
                        menuButtonSelectTrack.getItems().get(i).setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                // TODO 複数トラックの再統合を視野に入れても良い (つまり分割してから 2+4+5を統合して演奏する, みたいな)
                                menuButtonSelectTrack.setText(mMidiData.getTrackInfo()[currentLoopNumber]);
                                if (mMidiData != null) {
                                    mMidiData.setSelectedTrackIndex(currentLoopNumber);
                                }
                                convertButton.setDisable(false);
                            }
                        });
                    }
                }
            }
            event.setDropCompleted(HAS_DB_FILES);
            event.consume();
        }

    @FXML
        public void convertData() {
            if (mMidiData != null) {
                mPlayer = new MultiTrackMidiPlayer(App.getKeyInput(), mMidiData.convert(), mMidiData.getTickMicroseconds());
                runButton.setDisable(false);
            }
        }

    @FXML
        private void gorunButton() throws InterruptedException{
            int sleepMillisecond = 10000;
            // 再生遅延
            try{
                int parsedSleepTime = Integer.parseInt(delaySec.getText())*1000;
                sleepMillisecond = ckBoxTrackDivine.selectedProperty().get() ? parsedSleepTime : parsedSleepTime<=10000 ? parsedSleepTime : 10000 ;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (ckBoxTrackDivine.selectedProperty().get()) {
                Thread.sleep(sleepMillisecond);
                // 別スレッドで再生開始
                if (midiData != null) {
                    player = new MidiPlayer(App.getKeyInput(), midiData, midiData.getTickInMilliSeconds());
                    player.start();
                    stopButton.setDisable(false);
                } else {
                    Gui.logger().error("The midi file has not been converted correctly or is not working properly.");
                }
            } else {
                if (mPlayer != null) {
                    mPlayer.addAdvanceDelay(sleepMillisecond);
                    mPlayer.start();
                    stopButton.setDisable(false);
                }
            }
        }

    @FXML
        private void stopButton(){
            // 破棄
            clearPlayer();
            // Gui起動時の状態にリセット
            runButton.setDisable(true);
            stopButton.setDisable(true);
            convertButton.setDisable(true);
            ckBoxTrackDivine.setDisable(false);
            menuButtonSelectTrack.setDisable(true);
        };
    
    private void clearPlayer() {
        if (player != null) {
            if (player.isAlive()) {
                player.interrupt();
                player = null;
                midiData = null;
            } else {
                player = null;
                midiData = null;
            }
        }
        if (mPlayer != null) {
            if (mPlayer.isAlive()) {
                mPlayer.interrupt();
                mPlayer = null;
                mMidiData = null;
            } else {
                mPlayer = null;
                mMidiData = null;
            }
        }
    }
}
