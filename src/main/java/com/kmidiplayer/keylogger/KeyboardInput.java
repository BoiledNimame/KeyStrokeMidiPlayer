package com.kmidiplayer.keylogger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.ConfigHolder;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public class KeyboardInput {
    private final Logger logger = LogManager.getLogger("[KEY_INJECTER]");

    public KeyboardInput() {
        isDebug = ConfigHolder.instance().isDebug();
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

    // よりシンプルに
    public void keyInput(User32 user32, WinDef.HWND hWnd, boolean isDown, int vkCode) {
        // hWnd(ウィンドウ)がnullでなければ続行
        if (hWnd != null) {
            WinUser.INPUT wInput = new WinUser.INPUT();
            // WM_KEYメッセージを設定する
            wInput.type = new WinDef.DWORD(WinUser.INPUT.INPUT_KEYBOARD);
            wInput.input.setType("ki");
            wInput.input.ki.wScan = new WinDef.WORD(0);
            wInput.input.ki.time = new WinDef.DWORD(0);
            wInput.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR(0);
            wInput.input.ki.wVk = new WinDef.WORD(vkCode);
            if (isDown) {
                // 0=KEYDOWN
                wInput.input.ki.dwFlags = new WinDef.DWORD(0);
            } else {
                // 2=KEYUP
                wInput.input.ki.dwFlags = new WinDef.DWORD(2);
            }
            if (isDebug) {
                logger.info("sending" + vkCode + "key to window");
            }
            user32.SendInput(new WinDef.DWORD(1), (WinUser.INPUT[]) wInput.toArray(1), wInput.size());
        } else {
            logger.warn("ウィンドウが見つかりませんでした。");
        }
    }
}
