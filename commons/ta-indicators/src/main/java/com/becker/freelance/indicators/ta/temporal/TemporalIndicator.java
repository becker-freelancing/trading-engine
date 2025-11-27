package com.becker.freelance.indicators.ta.temporal;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;

import java.time.LocalDateTime;

public interface TemporalIndicator<V> {

    public V getValue(LocalDateTime time);


    int getUnstableBars();

    public TemporalBarSeries getBarSeries();

    default void reset() {
        getBarSeries().reset();
        if (this instanceof CachableIndicator<?, ?> cache) {
            cache.clearCache();
        }
    }
}
