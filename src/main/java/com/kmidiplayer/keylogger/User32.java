package com.kmidiplayer.keylogger;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface User32 extends Library {
    // 注意: https://github.com/caprica/vlcj/issues/682
    User32 INSTANCE = (User32) Native.load("user32", User32.class);
}