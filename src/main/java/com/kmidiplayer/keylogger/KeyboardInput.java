package com.kmidiplayer.keylogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kmidiplayer.App;
import com.kmidiplayer.json.ConfigLoader;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public class KeyboardInput {

    /*
     * Java Native Acsess
     * refernce:
     * 
     *  "Sending a Keyboard Input with Java JNA and SendInput()" -stackoverflow
     *   -> jnaによるuser32を利用したキー入力のデモ 古い(2016)が参考にできるコードがない…(gitにないのでない)
     *      https://stackoverflow.com/questions/28538234/sending-a-keyboard-input-with-java-jna-and-sendinput
    */

    private static String windowName = "Core";
    private static boolean isCopyNearestNote = true;
    private static boolean forceUsingVKCode = false;
    private static int noteRangeMax = 127;
    private static int noteRangeMin = 0;
    private static Map<String, String> config = new HashMap<String, String>(){{}};
    private static int vkCode = 0;
    private static int noteNumberOffset = 0;
    public static int occurrencesOfOutOfRangeMax = 0;
    public static int valeOfOutOfRangeMax = 0;
    public static int occurrencesOfOutOfRangeMin = 0;
    public static int valeOfOutOfRangeMin = 0;

    private final static Logger logger = LogManager.getLogger();

    public static void IsCopyNearestNoteSetter(boolean bool){
        logger.info("IsCopyNearestNote = " + bool);
        isCopyNearestNote = bool;
    }

    public static void ForceUsingVKCodeSetter(boolean bool){
        logger.info("forceUsingVKCode = " + bool);
        forceUsingVKCode = bool;
    }

    public static boolean ForceUsingVKCodeGetter(){
        return forceUsingVKCode;
    }

    public static void WindowNameSetter(String str){
        logger.info("WindowName = " + str);
        windowName = str;
    }

    public static void NoteRangeSetter(int Nmax, int Nmin){
        logger.info("NoteRangeMax = " + Nmax);
        logger.info("NoteRangeMin = " + Nmin);
        noteRangeMax = Nmax;
        noteRangeMin = Nmin;
    }

    public static void NoteNumberOffset(int Offset){
        noteNumberOffset = Offset;
    }

    public static void KeyboardInputInitialization() throws JsonProcessingException, IOException{
        config = ConfigLoader.ConfigReader();
    }

    public static void KeyControl(int noteNumber, boolean itPush){

        int buffedNoteNumber = noteNumber + noteNumberOffset;

        if ( isCopyNearestNote == true ){
            if (buffedNoteNumber > noteRangeMax){
                occurrencesOfOutOfRangeMax++;
                if((noteRangeMax-buffedNoteNumber) < valeOfOutOfRangeMax){
                    valeOfOutOfRangeMax = (noteRangeMax-buffedNoteNumber);
                }
                vkCode = noteRangeMax;
            } else if (noteRangeMin > buffedNoteNumber){
                occurrencesOfOutOfRangeMin++;
                if(valeOfOutOfRangeMin < (noteRangeMin-buffedNoteNumber)){
                    valeOfOutOfRangeMin = (noteRangeMin-buffedNoteNumber);
                }
                vkCode = noteRangeMin;
            } else {
                if(forceUsingVKCode==false){
                    vkCode = KeycordMap.GetVKcode(config.get(Integer.toString(buffedNoteNumber)));
                } else {
                    vkCode = Integer.parseInt(config.get(Integer.toString(buffedNoteNumber)));
                }
            }
        } else {
            if(forceUsingVKCode==false){
                vkCode = KeycordMap.GetVKcode(config.get(Integer.toString(buffedNoteNumber)));
            } else {
                vkCode = Integer.parseInt(config.get(Integer.toString(buffedNoteNumber)));
            }
        }

        User32 user32 = User32.INSTANCE;
        WinDef.HWND hWnd = user32.FindWindow(null, windowName);

        // hWnd(ウィンドウ)がnullでなければ続行
        if (hWnd != null) {
            WinUser.INPUT input = new WinUser.INPUT();
            // WM_KEYメッセージを設定する
            input.type = new WinDef.DWORD(WinUser.INPUT.INPUT_KEYBOARD);
            input.input.setType("ki");
            input.input.ki.wScan = new WinDef.WORD(0);
            input.input.ki.time = new WinDef.DWORD(0);
            input.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR(0);
            input.input.ki.wVk = new WinDef.WORD(vkCode);
            if(itPush =true){
                // 0=KEYDOWN
                input.input.ki.dwFlags = new WinDef.DWORD(0);
            } else {
                // 2=KEYUP
                input.input.ki.dwFlags = new WinDef.DWORD(2);
            }
            if(App.debugGetter()==true){ logger.info("sending" + vkCode + "key to window"); }
            user32.SendInput(new WinDef.DWORD(1), (WinUser.INPUT[]) input.toArray(1), input.size());

        } else {
            logger.info("ウィンドウが見つかりませんでした。");
        }
    }
}
