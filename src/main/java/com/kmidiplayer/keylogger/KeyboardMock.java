package com.kmidiplayer.keylogger;

public class KeyboardMock implements IInputter {
    KeyboardMock() {

    }
    // 模倣(出力テスト用)メソッド
    @Override
    public void keyInput(String windowName, boolean isDown, int vkCode) {
        LOGGER.debug("isPush: {}{}{}", isDown, isDown ? " , vkCode: " : ", vkCode: ", vkCode);
    }
}
