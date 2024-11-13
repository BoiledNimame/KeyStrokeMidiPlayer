package com.kmidiplayer.util;

import java.io.File;

public final class Resource {

    private static final String EMPTY_STRING = "";
    private static final String EXTENSION_STRING = "\\.";

    public static String getFileExtension(File file) {
        if (file.isFile()) {
            return file.getName().split(EXTENSION_STRING)[file.getName().split(EXTENSION_STRING).length - 1];
        } else {
            return EMPTY_STRING;
        }
    }

}
