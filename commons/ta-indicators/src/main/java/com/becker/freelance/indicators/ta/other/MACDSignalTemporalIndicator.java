package com.becker.freelance.indicators.ta.other;

import com.becker.freelance.commons.timeseries.TimeUtil;
import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class MACDSignalTemporalIndicator extends CachableIndicator<LocalDateTime, Decimal> implements TemporalIndicator<Decimal> {

    private final EMATemporalIndicator signalEma;
    private final MACDTemporalIndicator macd;
    private final boolean allowUsingLastAvailablePrice;

    public MACDSignalTemporalIndicator(MACDTemporalIndicator macd, int signalPeriod, boolean allowUsingLastAvailablePrice) {
        super(1000);
        this.macd = macd;
        this.allowUsingLastAvailablePrice = allowUsingLastAvailablePrice;
        this.signalEma = new EMATemporalIndicator(macd, signalPeriod, allowUsingLastAvailablePrice, false);
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
        return signalEma.getValue(index);
    }

    @Override
    public int getUnstableBars() {
        return signalEma.getUnstableBars() + macd.getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return signalEma.getBarSeries();
    }
}
