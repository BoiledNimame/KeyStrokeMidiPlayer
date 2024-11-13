package com.kmidiplayer.midi.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.kmidiplayer.config.YamlLoader;
import com.kmidiplayer.util.Resource;

import java.util.HashMap;

public class VkCodeMap {

    private final static String resourceLocation = Resource.getFileAbsolutePathAsString(VkCodeMap.class, "vkcode.yaml");

    private final static Map<String, Integer> KEYSTRING_VKCODE =
        YamlLoader.loadAsMap(resourceLocation).entrySet().stream()
                  .map(s -> new AbstractMap.SimpleEntry<>(s.getKey(), Integer.valueOf(s.getValue().toString())))
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, HashMap::new));

    public static int GetVKcode(String key){
        if(Objects.nonNull(KEYSTRING_VKCODE.get(key)) || KEYSTRING_VKCODE.containsKey(key)){
            return KEYSTRING_VKCODE.get(key);
        } else {
            throw new RuntimeException("tried to find the vkCode corresponding to ".concat(key).concat(" but it does not exist in ").concat(resourceLocation));
        }
    }
}