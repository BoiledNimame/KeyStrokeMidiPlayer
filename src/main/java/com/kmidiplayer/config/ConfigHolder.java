package com.kmidiplayer.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

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

    public void loadCommonSettings() {
        final JsonNode setting = JsonLoader.load("./generalsetting.json");

        isCopyNearestNote = getWithLogging(setting::get, "OutOfRangeCopyNearestNote", JsonNode::booleanValue);

        forceUsingVKCode = getWithLogging(setting::get, "forceUsingVKCode", JsonNode::booleanValue);

        windowName = getWithLogging(setting::get, "WindowName", JsonNode::textValue);

        useHighPrecisionMode = getWithLogging(setting::get, "HighPrecisionMode", JsonNode::booleanValue);

        noteRangeMax = getWithLogging(setting::get, "NoteMaxNumber", JsonNode::intValue);
        noteRangeMin = getWithLogging(setting::get, "NoteMinNumber", JsonNode::intValue);

        noteNumberOffset = getWithLogging(setting::get, "NoteNumberOffset", JsonNode::intValue);

        isDebug = getWithLogging(setting::get, "debug", JsonNode::booleanValue);

        keyMaps = JsonLoader.loadKeyMap(setting);
    }

    public <T, G> T getWithLogging(Function<String, G> getter, String key, Function<G, T> typeCaster) {
        final T result = typeCaster.apply(getter.apply(key));
        logger.info(key.concat(" = ").concat(Objects.nonNull(result) ? result.toString() : "value is not exist !"));
        return result;
    }
}