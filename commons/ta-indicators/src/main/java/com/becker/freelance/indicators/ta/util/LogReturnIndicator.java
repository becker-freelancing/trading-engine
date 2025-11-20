package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.indicator.TemporalIndicator;
import com.becker.freelance.indicators.ta.temporal.series.TemporalBarSeries;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Optional;

public class LogReturnIndicator extends CachableIndicator<LocalDateTime, Decimal> implements TemporalIndicator<Decimal> {

    private final TemporalIndicator<Decimal> closePrice;

    public LogReturnIndicator(TemporalIndicator<Decimal> closePrice) {
        super(1000);
        this.closePrice = closePrice;
    }

    @Override
    public Decimal getValue(LocalDateTime index) {
        Optional<Decimal> inCache = findInCache(index);

        if (inCache.isPresent()) {
            return inCache.get();
        }

        double v = closePrice.getValue(index).doubleValue();
        double v1 = closePrice.getValue(index.minus(getBarSeries().getPairDuration())).doubleValue();
        double log = Math.log(v / v1);
        Decimal decimalNum = new Decimal(log);
        putInCache(index, decimalNum);
        return decimalNum;
//        return closePrice.getValue(index).dividedBy(closePrice.getValue(index - 1)).log();
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

