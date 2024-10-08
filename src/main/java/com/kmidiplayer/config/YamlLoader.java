package com.kmidiplayer.config;
    
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlLoader {
    public static Map<String, Object> loadAsMap(String path) {
        final Yaml yaml = new Yaml();
        try (InputStream yamlData = new FileInputStream(Paths.get(path).toFile())) {
    
            @SuppressWarnings("unchecked")
            final Map<String, Object> dataMap = (Map<String, Object>) yaml.load(yamlData);

            return dataMap;
        } catch (InvalidPathException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}