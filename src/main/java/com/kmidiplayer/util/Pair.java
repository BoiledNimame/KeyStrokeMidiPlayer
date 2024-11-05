package com.kmidiplayer.util;

import java.util.function.Function;

public class Pair<K, V> {
    private final K key;
    private final V val;
    public Pair(K key, V value) {
        this.key = key;
        this.val = value;
    }
    public K getKey() { return key; }
    public V getValue() { return val; }
    public static <T, K, V> Function<T, Pair<K, V>> of(
        Function<? super T, ? extends K> keyMapper,
        Function<? super T, ? extends V> valueMapper
        ) {
        return (km) -> { return new Pair<>(keyMapper.apply(km), valueMapper.apply(km)); };
    }
}
