package com.kmidiplayer.config;

import java.util.function.Function;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Optionalっぽいクラス
 */
class Config<T, E> {

    private static final Logger logger = LogManager.getLogger("[Config]");

    final String key;
    final Function<String, E> getter;
    final Function<E, T> typeConverter;

    Config(String key, Function<String, E> getter, Function<E, T> typeConverter) {
        this.key = key;
        this.getter = getter;
        this.typeConverter = typeConverter;
    }

    Config(String key, Function<String, E> getter, Function<E, T> typeConverter, boolean isTestOutputEnabled) {
        this.key = key;
        this.getter = getter;
        this.typeConverter = typeConverter;

        if (isTestOutputEnabled) {
            getTestWithLogging();
        }
    }

    private void getTestWithLogging() {
        final T result = typeConverter.apply(getter.apply(key));
        logger.info(key.concat(" = ").concat(Objects.nonNull(result) ? result.toString() : "value is not exist !"));
    }

    T get() {
        return typeConverter.apply(getter.apply(key));
    }
}
