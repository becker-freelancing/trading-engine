package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.temporal.indicator.TemporalIndicator;
import com.becker.freelance.indicators.ta.temporal.series.TemporalBarSeries;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RollingMeanIndicator implements TemporalIndicator<Optional<Decimal>> {

    private final TemporalIndicator<Optional<Decimal>> baseIndicator;
    private final int meanPeriod;
    private final Decimal meanPeriodNum;
    private final Map<LocalDateTime, Decimal> cache = new HashMap<>();

    public RollingMeanIndicator(TemporalIndicator<Optional<Decimal>> baseIndicator, int meanPeriod) {
        this.baseIndicator = baseIndicator;
        this.meanPeriod = meanPeriod;
        this.meanPeriodNum = new Decimal(meanPeriod);
    }

    @Override
    public Optional<Decimal> getValue(LocalDateTime index) {
        cache.computeIfAbsent(index, idx -> {

            LocalDateTime start = index.minus(getBarSeries().getPairDuration().multipliedBy(meanPeriod - 1));
            if (start.isBefore(getBarSeries().getMinTime())) {
                return null;
            }
            double sum = 0.;
            while (start.isBefore(index) || start.isEqual(index)) {
                Optional<Decimal> value = baseIndicator.getValue(start);
                if (value.isEmpty()) {
                    return null;
                }
                sum += value.get().doubleValue();
                start = start.plus(getBarSeries().getPairDuration());
            }

            Decimal value = Decimal.valueOf(sum).divide(meanPeriodNum);
            return value;
        });
        return Optional.ofNullable(cache.get(index));
    }

    @Override
    public int getUnstableBars() {
        return baseIndicator.getUnstableBars() + meanPeriod;
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
