package com.kmidiplayer.config;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigHolder {

    public static final Configs configs = new Configs();

    public static class Configs {

        final Config<Boolean, Object> isDebug;
        final Config<String, Object> windowName;
        final Config<Boolean, Object> isCopyNearestNote;
        final Config<Boolean, Object> forceUsingVKCode;
        final Config<Integer, Object> noteNumberOffset;
        final Config<Integer, Object> initialDelay;
        final Map<String, String> keyMaps;

        boolean isMock;
        void setIsMock(boolean b) { isMock = b; }

        Configs() {
            final Map<String, Object> settings = YamlLoader.loadAsMap("./config.yaml");

            isDebug = new Config<>("debug", settings::get, ConfigValue::castBoolean);

            isCopyNearestNote = new Config<>("OutOfRangeCopyNearestNote", settings::get, ConfigValue::castBoolean);

            forceUsingVKCode = new Config<>("forceUsingVKCode", settings::get, ConfigValue::castBoolean);

            windowName = new Config<>("WindowName", settings::get, ConfigValue::castString);

            noteNumberOffset = new Config<>("NoteNumberOffset", settings::get, ConfigValue::castInt);

            initialDelay = new Config<>("initialDelay", settings::get, ConfigValue::castInt);

            keyMaps = YamlLoader.loadAsMap("./keymap.yaml").entrySet().stream()
                                .map(s -> new AbstractMap.SimpleEntry<String, String>(s.getKey(), s.getValue().toString()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new));

            isMock = false;
        }

        public Map<String, String> getKeyMap() { return keyMaps; };

        public boolean isDebug() { return isDebug.get(); }
        public String getWindowName() { return windowName.get(); }
        public boolean isCopyNearestNote() { return isCopyNearestNote.get(); }
        public boolean isUsingVkCode() { return forceUsingVKCode.get(); }
        public int getInitialDelay() { return initialDelay.get(); }
        public int getNoteOffset() { return noteNumberOffset.get(); }

        public boolean getIsMock() { return isMock; }
        public void setMockMode(boolean arg) { setIsMock(arg); }
    }
}