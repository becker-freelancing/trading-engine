package com.becker.freelance.indicators.ta.other;

import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.timeseries.TimeUtil;
import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class ATRTemporalIndicator extends CachableIndicator<LocalDateTime, Decimal> implements TemporalIndicator<Decimal> {

    private final TemporalBarSeries base;
    private final int period;
    private final Decimal decimalPeriod;
    private final boolean allowUsingLastAvailablePrice;
    private final boolean allowExternalStartValue;

    private final int precision;

    public ATRTemporalIndicator(TemporalBarSeries base, int period, boolean allowUsingLastAvailablePrice, boolean allowExternalStartValue) {
        super(1000);
        this.base = base;
        this.period = period;
        this.decimalPeriod = Decimal.valueOf(period);
        this.allowUsingLastAvailablePrice = allowUsingLastAvailablePrice;
        this.allowExternalStartValue = allowExternalStartValue;
        this.precision = base.getPair().precision();
    }

    @Override
    public Decimal getValue(LocalDateTime time) {
        if (allowUsingLastAvailablePrice && !getBarSeries().isTimeAligned(time)) {
            LocalDateTime lastAligned = TimeUtil.lastAligned(time, getBarSeries().getPairDuration());
            return getOrCompute(lastAligned);
        }
        return getOrCompute(time);
    }

    @Override
    protected Decimal computeMissing(LocalDateTime index) {
        if (getBarSeries().getMinTime().equals(index)) {
            return computeFirstTrueRange(index);
        }

        TimeSeriesEntry prev = getBarSeries().getEntry(getBarSeries().getLastTime(index));
        TimeSeriesEntry current = getBarSeries().getEntry(index);
        Decimal tr = max(
                current.getHighMid().subtract(current.getLowMid()),
                max(
                        current.getHighMid().subtract(prev.getCloseMid()).abs(),
                        current.getLowMid().subtract(prev.getCloseMid()).abs()
                )
        );

        Decimal prevAtr = getValue(getBarSeries().getLastTime(index));

        return prevAtr.multiply(period - 1).add(tr).divide(decimalPeriod).round(precision);
    }

    private Decimal max(Decimal d1, Decimal d2) {
        return d1.max(d2);
    }

    private Decimal computeFirstTrueRange(LocalDateTime index) {
        TimeSeriesEntry entry = getBarSeries().getEntry(index);
        return entry.getHighMid().subtract(entry.getLowMid());
    }

    @Override
    public int getUnstableBars() {
        return allowExternalStartValue ? 1 : period;
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return base;
    }
}
