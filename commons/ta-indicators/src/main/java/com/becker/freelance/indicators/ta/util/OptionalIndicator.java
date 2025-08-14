package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;

import java.util.Optional;

public record OptionalIndicator<T>(Indicator<T> baseIndicator) implements Indicator<Optional<T>> {
    @Override
    public Optional<T> getValue(int index) {
        return Optional.ofNullable(baseIndicator().getValue(index));
    }

    @Override
    public int getUnstableBars() {
        return baseIndicator().getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return baseIndicator().getBarSeries();
    }
}
