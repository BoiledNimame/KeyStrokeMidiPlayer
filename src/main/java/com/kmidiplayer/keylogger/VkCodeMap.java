package com.kmidiplayer.keylogger;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.kmidiplayer.config.YamlLoader;

import java.util.HashMap;

public class VkCodeMap {

    private final static String resourceLocation = Paths.get(getURI(VkCodeMap.class.getResource("vkcode.yaml"))).toAbsolutePath().toString();

    private final static Map<String, Integer> keyCodeMap =
        YamlLoader.loadAsMap(resourceLocation).entrySet().stream()
                  .map(s -> new AbstractMap.SimpleEntry<String, Integer>(s.getKey().toString(), Integer.valueOf(s.getValue().toString())))
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, HashMap::new));

    public static int GetVKcode(String key){
        if(Objects.nonNull(keyCodeMap.get(key)) || keyCodeMap.containsKey(key)){
            return Integer.valueOf(keyCodeMap.get(key));
        } else {
            throw new RuntimeException("tried to find the vkCode corresponding to ".concat(key).concat(" but it does not exist in ").concat(resourceLocation));
        }
    }

    private static URI getURI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}