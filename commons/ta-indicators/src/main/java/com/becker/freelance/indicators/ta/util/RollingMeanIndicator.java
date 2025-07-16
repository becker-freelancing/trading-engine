package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RollingMeanIndicator implements Indicator<Optional<Num>> {

    private final Indicator<Num> baseIndicator;
    private final int meanPeriod;
    private final Num meanPeriodNum;
    private final Map<Integer, Num> cache = new HashMap<>();

    public RollingMeanIndicator(Indicator<Num> baseIndicator, int meanPeriod) {
        this.baseIndicator = baseIndicator;
        this.meanPeriod = meanPeriod;
        this.meanPeriodNum = DecimalNum.valueOf(meanPeriod);
    }

    @Override
    public Optional<Num> getValue(int index) {
        cache.computeIfAbsent(index, idx -> {
            double sum = 0.;
            for (int i = index - meanPeriod + 1; i <= index; i++) {
                sum += baseIndicator.getValue(i).doubleValue();
            }

            Num value = DecimalNum.valueOf(sum).dividedBy(meanPeriodNum);
            return value;
        });
        return Optional.of(cache.get(index));
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
