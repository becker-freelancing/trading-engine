package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RollingMeanIndicator implements Indicator<Optional<Num>> {

    private final Indicator<Optional<Num>> baseIndicator;
    private final int meanPeriod;
    private final Num meanPeriodNum;
    private final Map<Integer, Num> cache = new HashMap<>();

    public RollingMeanIndicator(Indicator<Optional<Num>> baseIndicator, int meanPeriod) {
        this.baseIndicator = baseIndicator;
        this.meanPeriod = meanPeriod;
        this.meanPeriodNum = DecimalNum.valueOf(meanPeriod);
    }

    @Override
    public Optional<Num> getValue(int index) {
        cache.computeIfAbsent(index, idx -> {
            if (index - meanPeriod + 1 < 0) {
                return null;
            }
            double sum = 0.;
            for (int i = index - meanPeriod + 1; i <= index; i++) {
                Optional<Num> value = baseIndicator.getValue(i);
                if (value.isEmpty()) {
                    return null;
                }
                sum += value.get().doubleValue();
            }

            Num value = DecimalNum.valueOf(sum).dividedBy(meanPeriodNum);
            return value;
        });
        return Optional.ofNullable(cache.get(index));
    }

    @Override
    public int getUnstableBars() {
        return baseIndicator.getUnstableBars() + meanPeriod;
    }

    @Override
    public BarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
