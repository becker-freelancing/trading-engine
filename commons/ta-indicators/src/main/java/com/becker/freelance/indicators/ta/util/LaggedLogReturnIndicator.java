package com.becker.freelance.indicators.ta.util;


import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class LaggedLogReturnIndicator implements TemporalIndicator<Decimal> {

    private final TemporalIndicator<Decimal> closePrice;
    private final int lag;

    public LaggedLogReturnIndicator(TemporalIndicator<Decimal> closePrice, int lag) {
        this.closePrice = closePrice;
        this.lag = lag;
    }

    @Override
    public Decimal getValue(LocalDateTime index) {
        return closePrice.getValue(index).divide(closePrice.getValue(index.minus(getBarSeries().getPairDuration().multipliedBy(lag)))).log();
    }

    @Override
    public int getUnstableBars() {
        return closePrice.getUnstableBars() + lag + 1;
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return closePrice.getBarSeries();
    }
}
