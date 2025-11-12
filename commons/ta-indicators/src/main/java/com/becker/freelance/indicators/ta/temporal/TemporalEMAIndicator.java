package com.becker.freelance.indicators.ta.temporal;

import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.time.LocalDateTime;

public class TemporalEMAIndicator implements TemporalIndicator<Num> {

    private final EMAIndicator emaIndicator;
    private final boolean allowUsingLastAvailablePrice;
    private final TemporalBarSeries barSeries;

    public TemporalEMAIndicator(TemporalIndicator<Num> indicator, int period, boolean allowUsingLastAvailablePrice) {
        this(indicator.getBarSeries(),
                new EMAIndicator(new TemporalIndicatorToIndicator<>(indicator), period), allowUsingLastAvailablePrice);
    }

    public TemporalEMAIndicator(TemporalBarSeries barSeries, EMAIndicator emaIndicator, boolean allowUsingLastAvailablePrice) {
        this.barSeries = barSeries;
        this.emaIndicator = emaIndicator;
        this.allowUsingLastAvailablePrice = allowUsingLastAvailablePrice;
    }

    public TemporalEMAIndicator(TemporalIndicator<Num> indicator, int period) {
        this(indicator.getBarSeries(),
                new EMAIndicator(new TemporalIndicatorToIndicator<>(indicator), period), false);
    }

    public TemporalEMAIndicator(TemporalBarSeries barSeries, EMAIndicator emaIndicator) {
        this(barSeries, emaIndicator, false);
    }

    @Override
    public Num getValue(LocalDateTime time) {
        if (barSeries.getSize() < emaIndicator.getUnstableBars()) {
            return DecimalNum.ZERO;
        }
        int i = allowUsingLastAvailablePrice ? barSeries.mapTimeToLastAvailableIndex(time) : barSeries.mapTimeToIndex(time);
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
