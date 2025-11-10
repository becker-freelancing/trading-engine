package com.becker.freelance.indicators.ta.temporal;

import org.ta4j.core.num.Num;

import java.time.LocalDateTime;

public class TemporalHighPriceIndicator implements TemporalIndicator<Num> {

    private final TemporalBarSeries temporalBarSeries;

    public TemporalHighPriceIndicator(TemporalBarSeries temporalBarSeries) {
        this.temporalBarSeries = temporalBarSeries;
    }

    @Override
    public Num getValue(LocalDateTime time) {
        return temporalBarSeries.getBarSeries().getBar(temporalBarSeries.mapTimeToIndex(time)).getHighPrice();
    }

    @Override
    public int getUnstableBars() {
        return 0;
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return temporalBarSeries;
    }
}
