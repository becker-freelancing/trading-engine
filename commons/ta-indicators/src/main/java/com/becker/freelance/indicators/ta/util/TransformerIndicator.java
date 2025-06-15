package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;

import java.util.function.Function;

public class TransformerIndicator<S, V> implements Indicator<V> {

    private final Indicator<S> baseIndicator;
    private final Function<S, V> function;

    public TransformerIndicator(Indicator<S> baseIndicator, Function<S, V> function) {
        this.baseIndicator = baseIndicator;
        this.function = function;
    }

    @Override
    public V getValue(int index) {
        return function.apply(baseIndicator.getValue(index));
    }

    @Override
    public int getUnstableBars() {
        return baseIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
