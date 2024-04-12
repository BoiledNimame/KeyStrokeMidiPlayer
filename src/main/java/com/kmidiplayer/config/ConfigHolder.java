package com.kmidiplayer.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public void applyLaunchArgs(String[] args) {
        List<String> arglist = Arrays.asList(args);
        isDebug = !arglist.isEmpty() ? arglist.contains("-debug") : false;
        mockMode = !arglist.isEmpty() ? arglist.contains("-mock") : false;
        arglist = null;
    }

    public void loadCommonSettings() {
        final JsonNode setting = JsonLoader.generalSettingLoad();

        isCopyNearestNote = setting.get("OutOfRangeCopyNearestNote").booleanValue();
        logger.info("IsCopyNearestNote = " + isCopyNearestNote);
    
        forceUsingVKCode = setting.get("forceUsingVKCode").booleanValue();
        logger.info("forceUsingVKCode = " + forceUsingVKCode);
        
        windowName = setting.get("WindowName").textValue();
        logger.info("WindowName = " + windowName);
    
        noteRangeMax = setting.get("NoteMaxNumber").intValue();
        noteRangeMin = setting.get("NoteMinNumber").intValue();
        logger.info("NoteRangeMax = " + noteRangeMax);
        logger.info("NoteRangeMin = " + noteRangeMin);
        
        noteNumberOffset = setting.get("NoteNumberOffset").intValue();

        isDebug = setting.get("debug").booleanValue();

        keyMaps = JsonLoader.keyMapRead(setting);
    }
}