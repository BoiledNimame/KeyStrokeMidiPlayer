package com.kmidiplayer.midi.util;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import io.github.palexdev.materialfx.utils.StringUtils;

public class MidiFileChecker {

    public static boolean isValid(File file) {
        final String fileExtension = getFileExtension(file);
        if (StringUtils.EMPTY.equals(fileExtension)) {
            return false;
        } else {
            if (fileExtension.equalsIgnoreCase("mid") | fileExtension.equalsIgnoreCase("midi")) {
                try {
                    return MidiSystem.getSequence(file) != null;
                } catch (InvalidMidiDataException | IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

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
