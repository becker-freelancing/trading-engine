package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.indicator.TemporalIndicator;
import com.becker.freelance.indicators.ta.temporal.series.TemporalBarSeries;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Optional;

public class VolatilityIndicator extends CachableIndicator<LocalDateTime, Double> implements TemporalIndicator<Optional<Double>> {

    private final TemporalIndicator<Optional<Double>> varianceIndicator;

    public VolatilityIndicator(TemporalIndicator<Decimal> closePriceIndicator, int period) {
        super(1000);
        LogReturnIndicator logReturnIndicator = new LogReturnIndicator(closePriceIndicator);
        RollingMeanIndicator rollingMeanIndicator = new RollingMeanIndicator(new OptionalIndicator<>(logReturnIndicator), period);
        this.varianceIndicator = new RollingVarianceIndicator(rollingMeanIndicator, period);
    }

    @Override
    public Optional<Double> getValue(LocalDateTime index) {
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
    public TemporalBarSeries getBarSeries() {
        return varianceIndicator.getBarSeries();
    }
}
