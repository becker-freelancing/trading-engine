package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.time.LocalDateTime;
import java.util.Optional;

public class LogReturnIndicator extends CachableIndicator<LocalDateTime, Num> implements TemporalIndicator<Num> {

    private final TemporalIndicator<Num> closePrice;

    public LogReturnIndicator(TemporalIndicator<Num> closePrice) {
        super(1000);
        this.closePrice = closePrice;
    }

    @Override
    public Num getValue(LocalDateTime index) {
        Optional<Num> inCache = findInCache(index);

        if (inCache.isPresent()) {
            return inCache.get();
        }

        double v = closePrice.getValue(index).doubleValue();
        double v1 = closePrice.getValue(index.minus(getBarSeries().getPairDuration())).doubleValue();
        double log = Math.log(v / v1);
        DecimalNum decimalNum = DecimalNum.valueOf(log);
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

