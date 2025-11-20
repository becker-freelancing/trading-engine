package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.temporal.indicator.TemporalIndicator;
import com.becker.freelance.indicators.ta.temporal.series.TemporalBarSeries;

import java.time.LocalDateTime;
import java.util.Optional;

public record OptionalIndicator<T>(TemporalIndicator<T> baseIndicator) implements TemporalIndicator<Optional<T>> {
    @Override
    public Optional<T> getValue(LocalDateTime index) {
        return Optional.ofNullable(baseIndicator().getValue(index));
    }

    @Override
    public int getUnstableBars() {
        return baseIndicator().getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return baseIndicator().getBarSeries();
    }
}
