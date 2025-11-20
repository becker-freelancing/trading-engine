package com.becker.freelance.indicators.ta.other;

import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class HighPriceTemporalIndicator implements TemporalIndicator<Decimal> {


    private final TemporalBarSeries barSeries;

    public HighPriceTemporalIndicator(TemporalBarSeries barSeries) {
        this.barSeries = barSeries;
    }

    @Override
    public Decimal getValue(LocalDateTime index) {
        return barSeries.getEntry(index).getHighMid();
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
