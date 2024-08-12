package com.kmidiplayer.config;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmidiplayer.application.Main;

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

public class JsonLoader {

    public static JsonNode loadGeneralSetting() {
        try {
            return (new ObjectMapper()).readTree(Paths.get("./generalsetting.json").toFile());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, String> loadKeyMap(JsonNode generalSetting) {
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
                        if (generalSetting.get("forceUsingVKCode").booleanValue()){
                            if (isDebug){
                                Main.logger().info("{ try to getting (int) config.json(" + confgIndex + ") }");
                            }
                            put( Integer.toString(confgIndex) , config.get(Integer.toString(confgIndex)).textValue());
                        } else {
                            if (isDebug){
                                Main.logger().info("{ try to getting (str) config.json(" + confgIndex + ") }");
                            }
                            put( Integer.toString(confgIndex) , config.get(Integer.toString(confgIndex)).textValue());
                        }
                    }
                } };
                configDataMap = dataMap;
            } catch (NullPointerException e) {
                Main.logger().debug("It is possible that Config could not be loaded. Value Range dump : ");
                Main.logger().debug("{ (int) ConfigLoader.Max = " + max + " }");
                Main.logger().debug("{ (int) ConfigLoader.Min = " + min + " }");
                Main.logger().debug("Dump some values because the Note key may not have loaded properly : ");
                Main.logger().debug("{ (str) config.json(70) = " + config.get("70").textValue() + " }");
                Main.logger().debug("{ (str) config.json(80) = " + config.get("80").textValue() + " }");
                Main.logger().debug("{ (str) config.json(90) = " + config.get("90").textValue() + " }");
                Main.logger().debug("PrintStackTrace : ");
                e.printStackTrace();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Main.logger().error("The contents of config.json may be incorrect.");
        } catch (IOException e) {
            e.printStackTrace();
            Main.logger().error("Can't find config.json.");
        }

        return configDataMap;
    }


}