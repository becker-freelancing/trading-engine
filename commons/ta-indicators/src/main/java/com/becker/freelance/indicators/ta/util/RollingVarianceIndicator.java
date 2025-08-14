package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RollingVarianceIndicator implements Indicator<Optional<Double>> {

    private final Indicator<Optional<Num>> baseIndicator;
    private final int variancePeriod;
    private Map<Integer, Double> cache = new HashMap<>();

    public RollingVarianceIndicator(Indicator<Optional<Num>> baseIndicator, int variancePeriod) {
        this.baseIndicator = baseIndicator;
        this.variancePeriod = variancePeriod;
    }

    @Override
    public Optional<Double> getValue(int index) {
        cache.computeIfAbsent(index, idx -> {
            if (index - variancePeriod + 1 < 0) {
                return null;
            }
            double sum = 0.;
            double sumsq = 0.;
            for (int i = index - variancePeriod + 1; i <= index; i++) {
                Optional<Num> value = baseIndicator.getValue(i);
                if (value.isEmpty()) {
                    return null;
                }
                double baseValue = value.get().doubleValue();
                sum += baseValue;
                sumsq += (baseValue * baseValue);
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
    public BarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
