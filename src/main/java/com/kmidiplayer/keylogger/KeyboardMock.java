package com.kmidiplayer.keylogger;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.User32;

public class KeyboardMock implements IInputter {
    // 模倣(出力テスト用)メソッド
    @Override
    public void keyInput(User32 user32, WinDef.HWND hWnd, boolean isDown, int vkCode) {
        LOGGER.debug((new StringBuilder()).append("isPush: ").append(isDown).append(isDown ? " , vkCode: " : ", vkCode: ").append(vkCode).toString());
    }
}