package com.kmidiplayer.keylogger;

import java.util.Objects;

import com.kmidiplayer.config.Options;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;

public class KeyboardInput implements IInputter {

    private final User32 user32 = User32.INSTANCE;

    KeyboardInput() {
        isDebug = Options.configs.isDebug();
    }

    /*
     * Java Native Access
     * reference:
     *
     *  "Sending a Keyboard Input with Java JNA and SendInput()" -stackoverflow
     *   -> jnaによるuser32を利用したキー入力のデモ 古い(2016)が参考にできるコードがない…(gitにないのでない)
     *      https://stackoverflow.com/questions/28538234/sending-a-keyboard-input-with-java-jna-and-sendinput
    */

    private final boolean isDebug;
    private HWND cache;

    // よりシンプルに
    @Override
    public void keyInput(String windowName, boolean isDown, int vkCode) {
        // どうせwindowNameは同一なのでhWnd(ウィンドウ)をキャッシュしておく
        // ついでにウィンドウが見つからなかった時今アクティブなウィンドウを勝手に拾っておく
        if (cache==null) {
            cache = Objects.nonNull(windowName)
                  ? Objects.isNull(user32.FindWindow(null, windowName))
                   ? user32.GetActiveWindow()
                   : user32.FindWindow(null, windowName)
                  : null; // windowNameがnullなら何かがおかしいのでもうここもnullにする
        }
        // 範囲外のvkCodeの場合は0xEにするようにしてあるのでその場合は処理をスキップする
        if (vkCode == 0xE) { return; }
        // hWndがnullでなければ続行
        if (cache != null) {
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
                LOGGER.info("sending{}key to window", vkCode);
            }
            user32.SendInput(new WinDef.DWORD(1), (WinUser.INPUT[]) wInput.toArray(1), wInput.size());
        } else {
            LOGGER.warn("ウィンドウが見つかりませんでした。");
        }
    }
}