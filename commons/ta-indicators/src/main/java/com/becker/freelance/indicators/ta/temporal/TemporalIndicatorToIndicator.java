package com.becker.freelance.indicators.ta.temporal;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;

import java.time.LocalDateTime;

class TemporalIndicatorToIndicator<V> implements Indicator<V> {

    private final TemporalIndicator<V> delegate;

    TemporalIndicatorToIndicator(TemporalIndicator<V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public V getValue(int index) {
        LocalDateTime time = delegate.getBarSeries().mapIndexToTime(index);
        return delegate.getValue(time);
    }

    @Override
    public int getUnstableBars() {
        return delegate.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return delegate.getBarSeries().getBarSeries();
    }
}
