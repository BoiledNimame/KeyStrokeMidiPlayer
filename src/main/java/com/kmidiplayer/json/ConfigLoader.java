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

    public static JsonNode generalSettingLoad() {
        try {
            return (new ObjectMapper()).readTree(Paths.get("./generalsetting.json").toFile());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, String> keyMapRead(KeyboardInput kInput, JsonNode generalSetting) {
        Map<String, String> configDataMap = null;

        try {
            final JsonNode config = (new ObjectMapper()).readTree(Paths.get("./config.json").toFile());
            final int max = generalSetting.get("NoteMaxNumber").intValue();
            final int min = generalSetting.get("NoteMinNumber").intValue();
            final boolean isDebug = generalSetting.get("debug").booleanValue();
            
            try {
                // 音階と押されるキーの対応map
                    Map<String, String> dataMap = new HashMap<String, String>(){ {
                    for(int confgIndex = min; confgIndex <= max; confgIndex++ ){
                        if (kInput.isForceUsingVKCode()){
                            if (isDebug){
                                System.out.println("{ try to getting (int) config.json(" + confgIndex + ") }");
                            }
                            put( Integer.toString(confgIndex) , config.get(Integer.toString(confgIndex)).textValue());
                        } else {
                            if (isDebug){
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
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            App.logger().error("The contents of config.json may be incorrect.");
        } catch (IOException e) {
            e.printStackTrace();
            App.logger().error("Can't find config.json.");
        }
        
        return configDataMap;
    }

    
}