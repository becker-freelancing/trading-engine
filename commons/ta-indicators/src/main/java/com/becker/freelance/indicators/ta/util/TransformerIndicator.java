package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.temporal.indicator.TemporalIndicator;
import com.becker.freelance.indicators.ta.temporal.series.TemporalBarSeries;

import java.time.LocalDateTime;
import java.util.function.Function;

public class TransformerIndicator<S, V> implements TemporalIndicator<V> {

    private final TemporalIndicator<S> baseIndicator;
    private final Function<S, V> function;

    public TransformerIndicator(TemporalIndicator<S> baseIndicator, Function<S, V> function) {
        this.baseIndicator = baseIndicator;
        this.function = function;
    }

    @Override
    public V getValue(LocalDateTime index) {
        return function.apply(baseIndicator.getValue(index));
    }

    @Override
    public int getUnstableBars() {
        return baseIndicator.getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
