package com.kmidiplayer.json;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmidiplayer.App;
import com.kmidiplayer.keylogger.KeyboardInput;

    /*
     * Json ConfigLoader
     * refernce(JP):
     * 
     *  "JavaでJSONを操作するには"
     *   -> 初着手なjsonの扱い方について軽く参考にした
     *       https://camp.trainocate.co.jp/magazine/java-json/
     * 
     *  "Map (Java platform SE8)" -JavaDoc
     *   -> ちょいちょい使ってるHashMap、Jsonの記述と似ているので扱いやすい
     *      https://docs.oracle.com/javase/jp/8/docs/api/java/util/Map.html
    */

public class ConfigLoader {

    private static Map<String, String> configDataMap = null;

    public static Map<String, String> ConfigReader() throws JsonProcessingException, IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode config = jsonMapper.readTree(Paths.get("./config.json").toFile());
        
        int max = config.get("NoteMaxNumber").intValue();
        int min = config.get("NoteMinNumber").intValue();
        
        try {
            final KeyboardInput keyInput = App.getKeyInput();
            // 音階関連以外の設定情報setter
            keyInput.WindowNameSetter(config.get("WindowName").textValue());
            keyInput.IsCopyNearestNoteSetter(config.get("OutOfRangeCopyNearestNote").booleanValue());
            keyInput.NoteNumberOffset(config.get("NoteNumberOffset").intValue());
            keyInput.NoteRangeSetter(max, min);
            keyInput.ForceUsingVKCodeSetter(config.get("forceUsingVKCode").booleanValue());
            App.debugSetter(config.get("debug").booleanValue());

            // 音階と押されるキーの対応map
                Map<String, String> dataMap = new HashMap<String, String>(){ {
                for(int confgIndex = min; confgIndex <= max; confgIndex++ ){
                    if (keyInput.ForceUsingVKCodeGetter()==true){
                        if (App.debugGetter()==true){
                            System.out.println("{ try to getting (int) config.json(" + confgIndex + ") }");
                        }
                        put( Integer.toString(confgIndex) , config.get(Integer.toString(confgIndex)).textValue());
                    } else {
                        if (App.debugGetter()==true){
                            System.out.println("{ try to getting (str) config.json(" + confgIndex + ") }");
                        }
                        put( Integer.toString(confgIndex) , config.get(Integer.toString(confgIndex)).textValue());
                    }
                }
            } };
            configDataMap = dataMap;
        } catch (NullPointerException e) {
            System.out.println("It is possible that Config could not be loaded. Value Range dump : ");
            System.out.println("{ (int) ConfigLoader.Max = " + max + " }");
            System.out.println("{ (int) ConfigLoader.Min = " + min + " }");
            System.out.println("Dump some values because the Note key may not have loaded properly : ");
            System.out.println("{ (str) config.json(70) = " + config.get("70").textValue() + " }");
            System.out.println("{ (str) config.json(80) = " + config.get("80").textValue() + " }");
            System.out.println("{ (str) config.json(90) = " + config.get("90").textValue() + " }");
            System.out.println("PrintStackTrace : ");
            e.printStackTrace();
        }
        return configDataMap;
    }
}