package com.kmidiplayer.keylogger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public interface IInputter {
    final Logger LOGGER = LogManager.getLogger("[KEY_INJECTER]");
    public void keyInput(User32 user32, WinDef.HWND hWnd, boolean isDown, int vkCode);
}