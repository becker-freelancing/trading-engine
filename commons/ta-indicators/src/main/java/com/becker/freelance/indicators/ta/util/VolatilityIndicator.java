package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.Optional;

public class VolatilityIndicator extends CachableIndicator<Integer, Double> implements Indicator<Optional<Double>> {

    private final Indicator<Optional<Double>> varianceIndicator;

    public VolatilityIndicator(Indicator<Num> closePriceIndicator, int period) {
        super(1000);
        LogReturnIndicator logReturnIndicator = new LogReturnIndicator(closePriceIndicator);
        RollingMeanIndicator rollingMeanIndicator = new RollingMeanIndicator(new OptionalIndicator<>(logReturnIndicator), period);
        this.varianceIndicator = new RollingVarianceIndicator(rollingMeanIndicator, period);
    }

    @Override
    public Optional<Double> getValue(int index) {
        Optional<Double> inCache = findInCache(index);
        if (inCache.isPresent()) {
            return inCache;
        }
        Optional<Double> variance = varianceIndicator.getValue(index).map(Math::sqrt);
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
