package com.becker.freelance.indicators.ta.temporal.indicator;

import com.becker.freelance.commons.timeseries.TimeUtil;
import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.series.TemporalBarSeries;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class MACDTemporalIndicator extends CachableIndicator<LocalDateTime, Decimal> implements TemporalIndicator<Decimal> {

    private final TemporalIndicator<Decimal> base;
    private final boolean allowUsingLastAvailablePrice;
    private final EMATemporalIndicator emaFast;
    private final EMATemporalIndicator emaSlow;

    public MACDTemporalIndicator(TemporalIndicator<Decimal> base, int macdShortPeriod, int macdLongPeriod, boolean allowUsingLastAvailablePrice, boolean allowExternalStartValue) {
        super(1000);
        this.base = base;
        this.allowUsingLastAvailablePrice = allowUsingLastAvailablePrice;
        this.emaFast = new EMATemporalIndicator(base, macdShortPeriod, allowUsingLastAvailablePrice, allowExternalStartValue);
        this.emaSlow = new EMATemporalIndicator(base, macdLongPeriod, allowUsingLastAvailablePrice, allowExternalStartValue);
    }

    public TemporalIndicator<Decimal> getHistogram(int macdSignalLinePeriod) {
        return new MACDHistogramTemporalIndicator(
                this,
                (MACDSignalTemporalIndicator) getSignalLine(macdSignalLinePeriod),
                allowUsingLastAvailablePrice
        );
    }

    public TemporalIndicator<Decimal> getSignalLine(int macdSignalLinePeriod) {
        return new MACDSignalTemporalIndicator(
                this,
                macdSignalLinePeriod,
                allowUsingLastAvailablePrice
        );
    }

    public EMATemporalIndicator getShortTermEma() {
        return emaFast;
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
        Decimal fast = emaFast.getValue(index);
        Decimal slow = emaSlow.getValue(index);

        return fast.subtract(slow);
    }

    @Override
    public int getUnstableBars() {
        return emaSlow.getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return base.getBarSeries();
    }
}
