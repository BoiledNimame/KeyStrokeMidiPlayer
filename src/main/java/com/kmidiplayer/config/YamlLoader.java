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

        logger.info("try to load \"{}\"", path);

        try (InputStream fileData = new FileInputStream(Paths.get(path).toFile())) {

            @SuppressWarnings("unchecked")
            final Map<String, Object> yamlMap = (Map<String, Object>) (new Yaml()).load(fileData);

            return yamlMap;
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("InCollect path:", e);
        } catch (IOException e) {
            throw new RuntimeException("Can't find File: " + path, e);
        }
    }
}