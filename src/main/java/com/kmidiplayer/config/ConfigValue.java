package com.kmidiplayer.config;

class ConfigValue {
    static boolean castBoolean(Object o) {
        return (boolean) o;
    }
    static int castInt(Object o) {
        return (int) o;
    }
    static String castString(Object o) {
        return (String) o;
    }
}
