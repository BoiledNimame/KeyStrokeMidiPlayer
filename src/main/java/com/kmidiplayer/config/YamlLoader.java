package com.kmidiplayer.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

public class YamlLoader {

    private static final Logger logger = LogManager.getLogger("[Yaml]");

    public static Map<String, Object> loadAsMap(String path) {
        final Yaml yaml = new Yaml();

        logger.info("try to load \"" + path +"\"");

        try (InputStream yamlData = new FileInputStream(Paths.get(path).toFile())) {

            @SuppressWarnings("unchecked")
            final Map<String, Object> dataMap = (Map<String, Object>) yaml.load(yamlData);

            return dataMap;
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Incollect path:", e);
        } catch (IOException e) {
            throw new RuntimeException("Can't find File: " + path, e);
        }
    }
}