package com.becker.freelance.indicators.ta.temporal;

import java.time.LocalDateTime;

public interface TemporalIndicator<V> {

    public V getValue(LocalDateTime time);


    int getUnstableBars();

    public TemporalBarSeries getBarSeries();

    default void reset() {
        getBarSeries().reset();
    }
}
