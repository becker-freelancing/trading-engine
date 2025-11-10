package com.becker.freelance.indicators.ta.temporal;

import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.num.Num;

import java.time.LocalDateTime;

public class TemporalEMAIndicator implements TemporalIndicator<Num> {

    private final EMAIndicator emaIndicator;
    private final TemporalBarSeries barSeries;

    public TemporalEMAIndicator(TemporalIndicator<Num> indicator, int period) {
        this(indicator.getBarSeries(),
                new EMAIndicator(new TemporalIndicatorToIndicator<>(indicator), period));
    }

    public TemporalEMAIndicator(TemporalBarSeries barSeries, EMAIndicator emaIndicator) {
        this.barSeries = barSeries;
        this.emaIndicator = emaIndicator;
    }

    @Override
    public Num getValue(LocalDateTime time) {
        int i = barSeries.mapTimeToIndex(time);
        return emaIndicator.getValue(i);
    }

    @Override
    public int getUnstableBars() {
        return emaIndicator.getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return barSeries;
    }
}
