package com.becker.freelance.indicators.ta.cache;

public abstract class CachableIndicator<K, V> {

    private final FixedSizeMap<K, V> cache;

    public CachableIndicator(int cacheSize) {
        cache = new FixedSizeMap<>(cacheSize);
    }

    protected void putInCache(K key, V value) {
        cache.put(key, value);
    }

    protected V getOrCompute(K index) {
        if (!cache.containsKey(index)) {
            putInCache(index, computeMissing(index));
        }
        return cache.get(index);
    }

    protected abstract V computeMissing(K index);

    public void clearCache() {
        cache.clear();
    }
}
