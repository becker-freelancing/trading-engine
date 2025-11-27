package com.becker.freelance.indicators.ta.other;

import com.becker.freelance.commons.timeseries.TimeUtil;
import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class MACDHistogramTemporalIndicator extends CachableIndicator<LocalDateTime, Decimal> implements TemporalIndicator<Decimal> {

    private final MACDTemporalIndicator macd;
    private final MACDSignalTemporalIndicator signal;
    private final boolean allowUsingLastAvailablePrice;

    public MACDHistogramTemporalIndicator(MACDTemporalIndicator macd, MACDSignalTemporalIndicator signal, boolean allowUsingLastAvailablePrice) {
        super(1000);
        this.macd = macd;
        this.signal = signal;
        this.allowUsingLastAvailablePrice = allowUsingLastAvailablePrice;
    }

    @Override
    public void reset() {
        TemporalIndicator.super.reset();
        macd.reset();
        signal.reset();
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
        Decimal m = macd.getValue(index);
        Decimal s = signal.getValue(index);

        return m.subtract(s);
    }

    @Override
    public int getUnstableBars() {
        return signal.getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return signal.getBarSeries();
    }
}
