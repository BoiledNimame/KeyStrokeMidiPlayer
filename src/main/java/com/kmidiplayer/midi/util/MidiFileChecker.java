package com.kmidiplayer.midi.util;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import com.kmidiplayer.util.Resource;

import io.github.palexdev.materialfx.utils.StringUtils;

public class MidiFileChecker {

    public static boolean isValid(File file) {
        final String fileExtension = Resource.getFileExtension(file);
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

}
