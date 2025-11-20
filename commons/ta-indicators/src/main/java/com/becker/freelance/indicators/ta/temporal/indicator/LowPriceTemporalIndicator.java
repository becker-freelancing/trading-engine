package com.becker.freelance.indicators.ta.temporal.indicator;

import com.becker.freelance.indicators.ta.temporal.series.TemporalBarSeries;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class LowPriceTemporalIndicator implements TemporalIndicator<Decimal> {


    private final TemporalBarSeries barSeries;

    public LowPriceTemporalIndicator(TemporalBarSeries barSeries) {
        this.barSeries = barSeries;
    }

    @Override
    public Decimal getValue(LocalDateTime index) {
        return barSeries.getEntry(index).getLowMid();
    }

    @Override
    public int getUnstableBars() {
        return 0;
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return barSeries;
    }
}
