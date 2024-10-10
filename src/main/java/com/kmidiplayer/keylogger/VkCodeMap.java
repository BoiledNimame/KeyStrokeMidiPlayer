package com.kmidiplayer.keylogger;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.kmidiplayer.config.YamlLoader;

import java.util.HashMap;

public class VkCodeMap {
    private final static String resourceLocation = "/vkcode.yaml";

    private final static Map<String, Integer> keyCodeMap =
        YamlLoader.loadAsMap(resourceLocation).entrySet().stream()
                  .map(s -> new AbstractMap.SimpleEntry<String, Integer>(s.getKey().toString(), Integer.valueOf(s.getValue().toString())))
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, HashMap::new));

    public static int GetVKcode(String key){
        if(Objects.nonNull(keyCodeMap.get(key)) || keyCodeMap.containsKey(key)){
            return (int) keyCodeMap.get(key);
        } else {
            throw new RuntimeException("tried to find the vkCode corresponding to ".concat(key).concat(" but it does not exist in ").concat(resourceLocation));
        }
    }
}