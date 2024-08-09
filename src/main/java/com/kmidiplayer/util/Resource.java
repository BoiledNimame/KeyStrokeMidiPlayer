package com.kmidiplayer.util;

import java.io.IOException;
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

    public static URL getFileURL(Class<?> location, String dir, String name) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(dir);
        Objects.requireNonNull(name);
        try (Stream<Path> stream = Files.list(Paths.get(location.getResource(dir).toURI()))) {
            return stream.filter(p -> name.equals(p.getFileName().toString())).findFirst().orElseThrow().toUri().toURL();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (NoSuchElementException e) {
            logger.error("The resource could not be found or did not exist. Did you forget the extension? : " + dir + "/" + name);
            throw new RuntimeException(e);
        }
    }

    public static String getFIleURLAsString(Class<?> location, String dir, String name) {
        return getFileURL(location, dir, name).toString();
    }
}
