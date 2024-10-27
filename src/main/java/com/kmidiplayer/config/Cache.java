package com.kmidiplayer.config;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cache {
    private static final File cacheFile = new File("./cache");

    private static final ObservableList<String> cache = FXCollections.observableArrayList(new ArrayList<>());

    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            throw new UnsupportedOperationException("Do not call initialization more than once");
        }
        try (BufferedReader textReader = new BufferedReader(new FileReader(cacheFile))) {
            String line;
            while (Objects.nonNull(line = textReader.readLine())) {
                if (Files.exists(Paths.get(line))) {
                    cache.add(line);
                }
            }
            initialized = true;
        } catch (FileNotFoundException e1){
            try {
                Files.write(cacheFile.toPath(), new byte[]{});
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
            initialized = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObservableList<String> getCache() {
        if (!initialized) { throw new UnsupportedOperationException(); }
        return cache;
    }

    public static void toCache(List<String> list) {
        try {
            Files.write(cacheFile.toPath(), list, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
