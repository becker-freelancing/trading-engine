package com.becker.freelance.indicators.ta.util;


import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;

import java.time.Duration;
import java.time.LocalDateTime;

public class ShiftedIndicator implements TemporalIndicator<Decimal> {

    private final TemporalIndicator<Decimal> closePrice;
    private final Duration shift;
    private final int shiftCount;

    public ShiftedIndicator(TemporalIndicator<Decimal> closePrice, int shift) {
        this.closePrice = closePrice;
        this.shift = getBarSeries().getPairDuration().multipliedBy(shift);
        this.shiftCount = shift;
    }

    @Override
    public Decimal getValue(LocalDateTime index) {
        return closePrice.getValue(index.minus(shift));
    }

    @Override
    public int getUnstableBars() {
        return closePrice.getUnstableBars() + shiftCount;
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return closePrice.getBarSeries();
    }
}
