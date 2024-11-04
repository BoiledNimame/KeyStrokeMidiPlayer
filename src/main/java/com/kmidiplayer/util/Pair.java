package com.kmidiplayer.util;

public class Pair<K, V> {
    private final K key;
    private final V val;
    public Pair(K key, V value) {
        this.key = key;
        this.val = value;
    }
    public K getKey() { return key; }
    public V getValue() { return val; }
}
