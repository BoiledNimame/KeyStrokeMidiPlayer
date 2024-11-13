package com.kmidiplayer.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Resource {
    private final static Logger logger = LogManager.getLogger("[Util]");

    public static URL getFileURL(Class<?> location, String dirctoryFirst, String fileName) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(dirctoryFirst);
        Objects.requireNonNull(fileName);
        try (Stream<Path> stream = Files.list(Paths.get(Objects.requireNonNull(location.getResource(dirctoryFirst)).toURI()))) {
            return stream.filter(p -> fileName.equals(p.getFileName().toString())).findFirst().orElseThrow().toUri().toURL();
        } catch (IOException | URISyntaxException e) {
            logger.info("cannot Found file {}/{}", dirctoryFirst, fileName);
            throw new RuntimeException(e);
        } catch (NoSuchElementException e) {
            logger.error("The resource could not be found or did not exist. Did you forget the extension? : {}/{}", dirctoryFirst, fileName);
            throw new RuntimeException(e);
        }
    }

    public static String getFileAbsolutePathAsString(Class<?> location, String fileName) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(fileName);
        return Paths.get(getURI(Objects.requireNonNull(location.getResource(fileName)))).toAbsolutePath().toString();
    }

    public static String getFIleURLAsString(Class<?> location, String dir, String name) {
        return getFileURL(location, dir, name).toString();
    }

    private static final String EMPTY_STRING = "";
    private static final String EXTENSION_STRING = "\\.";

    public static String getFileExtension(File file) {
        if (file.isFile()) {
            return file.getName().split(EXTENSION_STRING)[file.getName().split(EXTENSION_STRING).length - 1];
        } else {
            return EMPTY_STRING;
        }
    }

    public static URI getURI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
