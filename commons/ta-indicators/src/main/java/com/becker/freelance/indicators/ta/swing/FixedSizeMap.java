package com.becker.freelance.indicators.ta.swing;

import java.util.*;

class FixedSizeMap<K, V> implements Map<K, V> {

    private final int size;
    private final Map<K, V> content;
    private final List<K> keyList;

    public FixedSizeMap(int size) {
        this.size = size;
        content = new HashMap<>();
        keyList = new FixedSizeList<>(size);
    }


    @Override
    public int size() {
        return content.size();
    }

    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return content.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return content.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return content.get(key);
    }

    @Override
    public V put(K key, V value) {
        if (size() == size) {
            K oldestKey = keyList.remove(0);
            content.remove(oldestKey);
        }
        if (content.containsKey(key)) {
            keyList.remove(key);
        }
        keyList.add(key);
        return content.put(key, value);
    }

    @Override
    public V remove(Object key) {
        keyList.remove((V) key);
        return content.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((k, v) -> put(k, v));
    }

    @Override
    public void clear() {
        content.clear();
    }

    @Override
    public Set<K> keySet() {
        return content.keySet();
    }

    @Override
    public Collection<V> values() {
        return content.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return content.entrySet();
    }
}
