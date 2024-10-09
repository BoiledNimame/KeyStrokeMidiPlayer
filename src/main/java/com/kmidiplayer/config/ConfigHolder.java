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

        isCopyNearestNote = getWithLogging(setting, "OutOfRangeCopyNearestNote", JsonNode::booleanValue);

        forceUsingVKCode = getWithLogging(setting, "forceUsingVKCode", JsonNode::booleanValue);

        windowName = getWithLogging(setting, "WindowName", JsonNode::textValue);

        useHighPrecisionMode = getWithLogging(setting, "HighPrecisionMode", JsonNode::booleanValue);

        noteRangeMax = getWithLogging(setting, "NoteMaxNumber", JsonNode::intValue);
        noteRangeMin = getWithLogging(setting, "NoteMinNumber", JsonNode::intValue);

        noteNumberOffset = getWithLogging(setting, "NoteNumberOffset", JsonNode::intValue);

        isDebug = getWithLogging(setting, "debug", JsonNode::booleanValue);

        keyMaps = JsonLoader.loadKeyMap(setting);
    }

    public <T> T getWithLogging(JsonNode node, String key, Function<JsonNode, T> getter) {
        final T result = getter.apply(node.get(key));
        logger.info(key.concat(" = ").concat(Objects.nonNull(result) ? result.toString() : "value is not exist !"));
        return result;
    }
}