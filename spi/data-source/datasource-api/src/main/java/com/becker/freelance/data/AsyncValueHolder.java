package com.becker.freelance.data;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class AsyncValueHolder<K, V> {

    private final Map<K, V> values;
    private final Object lock;
    private final Set<K> readers;

    public AsyncValueHolder() {
        values = new HashMap<>();
        lock = new Object();
        readers = new HashSet<>();
    }

    public void put(K key, V value) {
        synchronized (lock) {
            values.put(key, value);
            lock.notifyAll();
        }
    }

    public V getOrRead(K key, Supplier<V> reader) {
        synchronized (lock) {
            if (!readers.contains(key)) {
                readers.add(key);
                put(key, reader.get());
            }
            return waitForEntry(key);
        }
    }

    public V waitForEntry(K key) {
        synchronized (lock) {
            while (!values.containsKey(key)) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
            return values.get(key);
        }
    }
}
