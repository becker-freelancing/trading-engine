package com.becker.freelance.indicators.ta.temporal.indicator;

import com.becker.freelance.indicators.ta.temporal.series.TemporalBarSeries;

import java.time.LocalDateTime;

public interface TemporalIndicator<V> {

    public V getValue(LocalDateTime time);


    int getUnstableBars();

    public TemporalBarSeries getBarSeries();

    default void reset() {
        getBarSeries().reset();
    }
}
