package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RollingVarianceIndicator implements TemporalIndicator<Optional<Double>> {

    private final TemporalIndicator<Optional<Decimal>> baseIndicator;
    private final int variancePeriod;
    private Map<LocalDateTime, Double> cache = new HashMap<>();

    public RollingVarianceIndicator(TemporalIndicator<Optional<Decimal>> baseIndicator, int variancePeriod) {
        this.baseIndicator = baseIndicator;
        this.variancePeriod = variancePeriod;
    }

    @Override
    public Optional<Double> getValue(LocalDateTime index) {
        cache.computeIfAbsent(index, idx -> {
            LocalDateTime start = index.minus(getBarSeries().getPairDuration().multipliedBy(variancePeriod - 1));
            if (start.isBefore(getBarSeries().getMinTime())) {
                return null;
            }
            double sum = 0.;
            double sumsq = 0.;

            while (start.isBefore(index) || start.equals(index)) {
                Optional<Decimal> value = baseIndicator.getValue(index);
                if (value.isEmpty()) {
                    return null;
                }
                double baseValue = value.get().doubleValue();
                sum += baseValue;
                sumsq += (baseValue * baseValue);
                start = start.plus(getBarSeries().getPairDuration());
            }
            double mean = sum / variancePeriod;
            double meanSq = sumsq / variancePeriod;
            double variance = meanSq - (mean * mean);
            return variance;
        });
        return Optional.ofNullable(cache.get(index));
    }

    @Override
    public int getUnstableBars() {
        return baseIndicator.getUnstableBars() + variancePeriod;
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
