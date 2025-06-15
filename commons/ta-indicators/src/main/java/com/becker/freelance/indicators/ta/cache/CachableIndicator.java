package com.becker.freelance.indicators.ta.cache;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CachableIndicator<K extends Comparable<K>, V> {

    private final FixedSizeMap<K, V> cache;

    public CachableIndicator(int cacheSize) {
        cache = new FixedSizeMap<>(cacheSize);
    }

    protected Optional<V> findInCache(K key) {
        if (cache.containsKey(key)) {
            V cacheItem = cache.get(key);
            return Optional.of(cacheItem);
        }
        return Optional.empty();
    }

    protected void putInCache(K key, V value) {
        cache.put(key, value);
    }

    protected Stream<V> values() {
        return cache.values().stream();
    }

    protected Stream<V> valuesSorted() {
        return cache.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue);
    }

    protected void removeFromCache(K index) {
        cache.remove(index);
    }
}
