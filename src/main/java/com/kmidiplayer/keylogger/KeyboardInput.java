package com.kmidiplayer.keylogger;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.kmidiplayer.json.ConfigLoader;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public class KeyboardInput {
    private final Logger logger = LogManager.getLogger("[KEY_INJECTER]");

    public KeyboardInput() {
        final JsonNode setting = ConfigLoader.generalSettingLoad();

        isCopyNearestNote = setting.get("OutOfRangeCopyNearestNote").booleanValue();
        logger.info("IsCopyNearestNote = " + isCopyNearestNote);
    
        forceUsingVKCode = setting.get("forceUsingVKCode").booleanValue();
        logger.info("forceUsingVKCode = " + forceUsingVKCode);
        
        windowName = setting.get("WindowName").textValue();
        logger.info("WindowName = " + windowName);
    
        noteRangeMax = setting.get("NoteMaxNumber").intValue();
        noteRangeMin = setting.get("NoteMinNumber").intValue();
        logger.info("NoteRangeMax = " + noteRangeMax);
        logger.info("NoteRangeMin = " + noteRangeMin);
        
        noteNumberOffset = setting.get("NoteNumberOffset").intValue();

        isDebug = setting.get("debug").booleanValue();

        config = ConfigLoader.keyMapRead(this, setting);
    }

    /*
     * Java Native Acsess
     * refernce:
     * 
     *  "Sending a Keyboard Input with Java JNA and SendInput()" -stackoverflow
     *   -> jnaによるuser32を利用したキー入力のデモ 古い(2016)が参考にできるコードがない…(gitにないのでない)
     *      https://stackoverflow.com/questions/28538234/sending-a-keyboard-input-with-java-jna-and-sendinput
    */

    private final boolean isDebug;
    private final String windowName;
    private final boolean isCopyNearestNote;
    private final boolean forceUsingVKCode;
    private final int noteRangeMax;
    private final int noteRangeMin;
    private final Map<String, String> config;
    private final int noteNumberOffset;

    public boolean isForceUsingVKCode(){
        return forceUsingVKCode;
    }

    public boolean isCopyNearestNote() {
        return isCopyNearestNote;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public Map<String, String> config() {
        return config;
    }

    public int getNoteNumberOffset() {
        return noteNumberOffset;
    }

    public boolean isForceUsingVkCode() {
        return forceUsingVKCode;
    }

    public String getWindowName() {
        return windowName;
    }

    /**
     * @return {Min, Max}
     */
    public int[] getNoteLimit() {
        return new int[]{noteRangeMin, noteRangeMax};
    }

    // よりシンプルに
    public void keyInput(User32 user32, WinDef.HWND hWnd, boolean isDown, int vkCode) {
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
            if (isDown =true) {
                // 0=KEYDOWN
                input.input.ki.dwFlags = new WinDef.DWORD(0);
            } else {
                // 2=KEYUP
                input.input.ki.dwFlags = new WinDef.DWORD(2);
            }
            if (isDebug) {
                logger.info("sending" + vkCode + "key to window");
            }
            user32.SendInput(new WinDef.DWORD(1), (WinUser.INPUT[]) input.toArray(1), input.size());

        } else {
            logger.info("ウィンドウが見つかりませんでした。");
        }
    }
}
