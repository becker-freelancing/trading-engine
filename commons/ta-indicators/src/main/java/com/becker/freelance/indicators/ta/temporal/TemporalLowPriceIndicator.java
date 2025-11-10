package com.becker.freelance.indicators.ta.temporal;

import org.ta4j.core.num.Num;

import java.time.LocalDateTime;

public class TemporalLowPriceIndicator implements TemporalIndicator<Num> {

    private final TemporalBarSeries temporalBarSeries;

    public TemporalLowPriceIndicator(TemporalBarSeries temporalBarSeries) {
        this.temporalBarSeries = temporalBarSeries;
    }

    @Override
    public Num getValue(LocalDateTime time) {
        return temporalBarSeries.getBarSeries().getBar(temporalBarSeries.mapTimeToIndex(time)).getLowPrice();
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
