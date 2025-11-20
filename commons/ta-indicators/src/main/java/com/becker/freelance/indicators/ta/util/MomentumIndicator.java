package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class MomentumIndicator implements TemporalIndicator<Decimal> {

    private final TemporalIndicator<Decimal> baseIndicator;
    private final TemporalIndicator<Decimal> subtractIndicator;

    public MomentumIndicator(TemporalIndicator<Decimal> baseIndicator, TemporalIndicator<Decimal> subtractIndicator) {
        this.baseIndicator = baseIndicator;
        this.subtractIndicator = subtractIndicator;
    }

    @Override
    public Decimal getValue(LocalDateTime index) {
        return baseIndicator.getValue(index).subtract(subtractIndicator.getValue(index));
    }

    @Override
    public int getUnstableBars() {
        return Math.max(baseIndicator.getUnstableBars(), subtractIndicator.getUnstableBars());
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
