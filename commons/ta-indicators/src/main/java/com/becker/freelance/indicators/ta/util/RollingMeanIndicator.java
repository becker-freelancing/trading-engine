package com.becker.freelance.indicators.ta.util;

import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RollingMeanIndicator implements TemporalIndicator<Optional<Num>> {

    private final TemporalIndicator<Optional<Num>> baseIndicator;
    private final int meanPeriod;
    private final Num meanPeriodNum;
    private final Map<LocalDateTime, Num> cache = new HashMap<>();

    public RollingMeanIndicator(TemporalIndicator<Optional<Num>> baseIndicator, int meanPeriod) {
        this.baseIndicator = baseIndicator;
        this.meanPeriod = meanPeriod;
        this.meanPeriodNum = DecimalNum.valueOf(meanPeriod);
    }

    @Override
    public Optional<Num> getValue(LocalDateTime index) {
        cache.computeIfAbsent(index, idx -> {

            LocalDateTime start = index.minus(getBarSeries().getPairDuration().multipliedBy(meanPeriod - 1));
            if (start.isBefore(getBarSeries().getMinTime())) {
                return null;
            }
            double sum = 0.;
            while (start.isBefore(index) || start.isEqual(index)) {
                Optional<Num> value = baseIndicator.getValue(start);
                if (value.isEmpty()) {
                    return null;
                }
                sum += value.get().doubleValue();
                start = start.plus(getBarSeries().getPairDuration());
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
    public TemporalBarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
