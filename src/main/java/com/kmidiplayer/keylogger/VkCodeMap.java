package com.kmidiplayer.keylogger;

import java.util.Map;
import java.util.Objects;

import com.kmidiplayer.util.ResourceLocation;

public class VkCodeMap {

    private final static Map<String, Integer> KEYSTRING_VKCODE =
        ResourceLocation.YAML_VKCODE.getYamlAsMap((v) -> Integer.valueOf(v.getValue().toString()));

    public static int GetVKcode(String key){
        if(Objects.nonNull(KEYSTRING_VKCODE.get(key)) || KEYSTRING_VKCODE.containsKey(key)){
            return KEYSTRING_VKCODE.get(key);
        } else {
            throw new RuntimeException("tried to find the vkCode corresponding to ".concat(key).concat(" but it does not exist in ").concat(VkCodeMap.class.getResource("vkcode.yaml").toString()));
        }
    }
}