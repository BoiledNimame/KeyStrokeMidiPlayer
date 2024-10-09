package com.kmidiplayer.config;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigHolder {
    private static final ConfigHolder instance = new ConfigHolder();
    public static ConfigHolder instance() { return instance; }

    private static final Logger logger = LogManager.getLogger("[Cfg]");

    private ConfigHolder() { }

    private Map<String, String> keyMaps;
    public void setKeyMap(Map<String, String> map) { keyMaps = map; }
    public Map<String, String> getKeyMap() { return keyMaps; }

    private boolean isDebug;
    public boolean isDebug() { return isDebug; }

    private String windowName;
    public String getWindowName() { return windowName; }

    private boolean isCopyNearestNote;
    public boolean isCopyNearestNote() { return isCopyNearestNote; }

    private boolean forceUsingVKCode;
    public boolean isUsingVkCode() { return forceUsingVKCode; }

    private int noteRangeMax;
    public int getMaxNote() { return noteRangeMax; }

    private int noteRangeMin;
    public int getMinNote() { return noteRangeMin; }

    private int noteNumberOffset;
    public int getNoteOffset() { return noteNumberOffset; }

    private boolean mockMode;
    public boolean isMockMode() { return mockMode; }

    private boolean useFxml;
    public boolean useFxml() { return useFxml; }

    private boolean useHighPrecisionMode;
    public boolean useHighPrecisionMode() { return useHighPrecisionMode; };

    public void applyLaunchArgs(String[] args) {
        List<String> arglist = Arrays.asList(args);
        isDebug  = !arglist.isEmpty() ? arglist.contains("-debug") : false;
        mockMode = !arglist.isEmpty() ? arglist.contains("-mock")  : false;
        useFxml  = !arglist.isEmpty() ? arglist.contains("-fxml")  : false;
        arglist = null;
    }

    public void loadCommonSettingsYaml() {
        logger.info("try to load \"./config.yaml\"");

        final Map<String, Object> settings = YamlLoader.loadAsMap("./config.yaml");

        isCopyNearestNote = getWithLogging(settings::get, "OutOfRangeCopyNearestNote", ClassCast::castBoolean);

        forceUsingVKCode = getWithLogging(settings::get, "forceUsingVKCode", ClassCast::castBoolean);

        windowName = getWithLogging(settings::get, "WindowName", ClassCast::castString);

        useHighPrecisionMode = getWithLogging(settings::get, "HighPrecisionMode", ClassCast::castBoolean);

        noteRangeMax = getWithLogging(settings::get, "NoteMaxNumber", ClassCast::castInt);
        noteRangeMin = getWithLogging(settings::get, "NoteMinNumber", ClassCast::castInt);

        noteNumberOffset = getWithLogging(settings::get, "NoteNumberOffset", ClassCast::castInt);

        isDebug = getWithLogging(settings::get, "debug", ClassCast::castBoolean);

        // keyMaps =
        YamlLoader.loadAsMap("./keymap.yaml").entrySet().stream()
                            .map(s -> new AbstractMap.SimpleEntry<String, String>(s.getKey(), s.getValue().toString()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new));
    }

    public <T, G> T getWithLogging(Function<String, G> getter, String key, Function<G, T> typeCaster) {
        final T result = typeCaster.apply(getter.apply(key));
        logger.info(key.concat(" = ").concat(Objects.nonNull(result) ? result.toString() : "value is not exist !"));
        return result;
    }
}