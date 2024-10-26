package com.kmidiplayer.util;

public class Pair<T, V> {
    private final T tag;
    private final V val;
    public Pair(T tag, V value) {
        this.tag = tag;
        this.val = value;
    }
    public T getTag() { return tag; }
    public V getValue() { return val; }
}
