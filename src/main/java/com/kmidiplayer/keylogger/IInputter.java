package com.kmidiplayer.keylogger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IInputter {
    Logger LOGGER = LogManager.getLogger("[KEY_INJECTOR]");
    void keyInput(String windowName, boolean isDown, int vkCode);
}
