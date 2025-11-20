package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class LogReturnIndicator extends CachableIndicator<LocalDateTime, Decimal> implements TemporalIndicator<Decimal> {

    private final TemporalIndicator<Decimal> closePrice;

    public LogReturnIndicator(TemporalIndicator<Decimal> closePrice) {
        super(1000);
        this.closePrice = closePrice;
    }

    @Override
    public Decimal getValue(LocalDateTime index) {
        return getOrCompute(index);
    }

    @Override
    protected Decimal computeMissing(LocalDateTime index) {
        double v = closePrice.getValue(index).doubleValue();
        double v1 = closePrice.getValue(index.minus(getBarSeries().getPairDuration())).doubleValue();
        double log = Math.log(v / v1);
        Decimal decimalNum = new Decimal(log);
        return decimalNum;
    }

    @Override
    public int getUnstableBars() {
        return closePrice.getUnstableBars() + 1;
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return closePrice.getBarSeries();
    }
}

