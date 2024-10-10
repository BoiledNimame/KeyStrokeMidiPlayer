package com.kmidiplayer.config;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigHolder {
    private static final ConfigHolder instance = new ConfigHolder();
    public static ConfigHolder instance() { return instance; }

    public static final Configs configs = new Configs();

    private ConfigHolder() { }

    public static class Configs {
        final Config<Boolean, Object> isDebug;
        final Config<String, Object> windowName;
        final Config<Boolean, Object> isCopyNearestNote;
        final Config<Boolean, Object> forceUsingVKCode;
        final Config<Integer, Object> noteRangeMax;
        final Config<Integer, Object> noteRangeMin;
        final Config<Integer, Object> noteNumberOffset;
        final Config<Boolean, Object> useHighPrecisionMode;

        final Map<String, String> keyMaps;

        Configs() {
            final Map<String, Object> settings = YamlLoader.loadAsMap("./config.yaml");

            isDebug = new Config<>("isDebug", settings::get, ClassCast::castBoolean);

            isCopyNearestNote = new Config<>("OutOfRangeCopyNearestNote", settings::get, ClassCast::castBoolean);

            forceUsingVKCode = new Config<>("forceUsingVKCode", settings::get, ClassCast::castBoolean);

            windowName = new Config<>("WindowName", settings::get, ClassCast::castString);

            useHighPrecisionMode = new Config<>("HighPrecisionMode", settings::get, ClassCast::castBoolean);

            noteRangeMax = new Config<>("NoteMaxNumber", settings::get, ClassCast::castInt);
            noteRangeMin = new Config<>("NoteMinNumber", settings::get, ClassCast::castInt);

            noteNumberOffset = new Config<>("NoteNumberOffset", settings::get, ClassCast::castInt);

            keyMaps = YamlLoader.loadAsMap("./keymap.yaml").entrySet().stream()
                                .map(s -> new AbstractMap.SimpleEntry<String, String>(s.getKey(), s.getValue().toString()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new));
        }

        public Map<String, String> getKeyMap() { return keyMaps; };

        public boolean isDebug() { return isDebug.get(); }
        public String getWindowName() { return windowName.get(); }
        public boolean isCopyNearestNote() { return isCopyNearestNote.get(); }
        public boolean isUsingVkCode() { return forceUsingVKCode.get(); }
        public int getMaxNote() { return noteRangeMax.get(); }
        public int getMinNote() { return noteRangeMin.get(); }
        public int getNoteOffset() { return noteNumberOffset.get(); }
        public boolean useHighPrecisionMode() { return useHighPrecisionMode.get(); };
    }
}