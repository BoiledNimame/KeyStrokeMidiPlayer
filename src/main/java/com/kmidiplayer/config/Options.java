package com.kmidiplayer.config;

import java.io.File;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.kmidiplayer.util.Cast;

public class Options {

    public static final Configs configs = new Configs();

    public static class Configs {

        final Config<Boolean, Object> isDebug;
        final Config<String, Object> windowName;
        final Config<Boolean, Object> forceUsingVKCode;
        final Config<Integer, Object> noteNumberOffset;
        final Config<Integer, Object> initialDelay;
        final Map<String, String> keyMaps;

        boolean isMock;
        void setIsMock(boolean b) { isMock = b; }
        boolean NoteUI;
        void setNoteUI(boolean b) { NoteUI = b; }
        boolean useRobot;
        void setUseRobot(boolean b) { useRobot = b; }

        Configs() {
            final Map<String, Object> settings = YamlLoader.loadAsMap(new File(System.getProperty("user.dir"), "config.yaml"));

            isDebug = new Config<>("debug", settings::get, Cast::toBoolean);

            forceUsingVKCode = new Config<>("forceUsingVKCode", settings::get, Cast::toBoolean);

            windowName = new Config<>("WindowName", settings::get, Cast::toString);

            noteNumberOffset = new Config<>("NoteNumberOffset", settings::get, Cast::toInt);

            initialDelay = new Config<>("initialDelay", settings::get, Cast::toInt);

            keyMaps = YamlLoader.loadAsMap(new File(System.getProperty("user.dir"), "keymap.yaml")).entrySet().stream()
                                .map(s -> new AbstractMap.SimpleEntry<>(s.getKey(), s.getValue().toString()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new));
        }

        public Map<String, String> getKeyMap() { return keyMaps; }

        public boolean isDebug() { return isDebug.get(); }
        public String getWindowName() { return windowName.get(); }
        public boolean isUsingVkCode() { return forceUsingVKCode.get(); }
        public int getInitialDelay() { return initialDelay.get(); }
        public int getNoteOffset() { return noteNumberOffset.get(); }

        /* launch args */

        public void applyLaunchArgs(List<String> args) {
            setIsMock(args.contains("-mock"));
            setNoteUI(args.contains("-noteUI"));
            setUseRobot(args.contains("-useRobot"));
        }

        public boolean getIsMock() { return isMock; }
        public boolean useNoteUI() { return NoteUI; }
        public boolean useRobot() { return useRobot; }
    }

    public static final Supplier<Integer> definedNoteMin = () -> Options.configs.getKeyMap().keySet().stream().mapToInt(Integer::parseInt).min().orElseThrow(IllegalArgumentException::new);
    public static final Supplier<Integer> definedNoteMax = () -> Options.configs.getKeyMap().keySet().stream().mapToInt(Integer::parseInt).max().orElseThrow(IllegalArgumentException::new);

}