package com.becker.freelance.indicators.ta.temporal;

import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

import java.time.LocalDateTime;

public class TemporalNumericIndicator implements TemporalIndicator<Num> {

    private final TemporalBarSeries barSeries;
    private final NumericIndicator indicator;

    public TemporalNumericIndicator(TemporalBarSeries barSeries, NumericIndicator indicator) {
        this.barSeries = barSeries;
        this.indicator = indicator;
    }

    @Override
    public Num getValue(LocalDateTime time) {
        return indicator.getValue(barSeries.mapTimeToIndex(time));
    }

    @Override
    public int getUnstableBars() {
        return indicator.getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return barSeries;
    }
}
