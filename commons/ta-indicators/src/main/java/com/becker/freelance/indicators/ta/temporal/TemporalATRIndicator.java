package com.becker.freelance.indicators.ta.temporal;

import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.num.Num;

import java.time.LocalDateTime;

public class TemporalATRIndicator implements TemporalIndicator<Num> {

    private final TemporalIndicator<Num> indicator;
    private final ATRIndicator atrIndicator;

    public TemporalATRIndicator(TemporalIndicator<Num> indicator, int period) {
        this.indicator = indicator;
        this.atrIndicator = new ATRIndicator(indicator.getBarSeries().getBarSeries(), period);
    }

    @Override
    public Num getValue(LocalDateTime time) {
        return atrIndicator.getValue(getBarSeries().mapTimeToIndex(time));
    }

    @Override
    public int getUnstableBars() {
        return atrIndicator.getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return indicator.getBarSeries();
    }
}
