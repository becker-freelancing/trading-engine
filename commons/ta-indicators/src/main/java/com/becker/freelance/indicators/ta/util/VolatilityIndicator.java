package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.util.Optional;

public class VolatilityIndicator extends CachableIndicator<Integer, Num> implements Indicator<Optional<Num>> {

    private final Indicator<Optional<Num>> varianceIndicator;

    public VolatilityIndicator(Indicator<Num> closePriceIndicator, int period) {
        super(1000);
        LogReturnIndicator logReturnIndicator = new LogReturnIndicator(closePriceIndicator);
        RollingMeanIndicator rollingMeanIndicator = new RollingMeanIndicator(logReturnIndicator, period);
        this.varianceIndicator = new RollingVarianceIndicator(rollingMeanIndicator, period);
    }

    @Override
    public Optional<Num> getValue(int index) {
        Optional<Num> inCache = findInCache(index);
        if (inCache.isPresent()) {
            return inCache;
        }
        Optional<Num> variance = varianceIndicator.getValue(index).map(Num::sqrt);
        variance.ifPresent(value -> putInCache(index, value));
        return variance;
    }

    @Override
    public int getUnstableBars() {
        return varianceIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return varianceIndicator.getBarSeries();
    }
}
