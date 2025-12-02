package com.becker.freelance.indicators.ta.other;

import com.becker.freelance.commons.timeseries.TimeUtil;
import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class EMATemporalIndicator extends CachableIndicator<LocalDateTime, Decimal> implements TemporalIndicator<Decimal> {

    private final TemporalIndicator<Decimal> base;
    private final int period;
    private final Decimal alpha;
    private final boolean allowUsingLastAvailablePrice;
    private final boolean allowExternalStartValue;
    private final Decimal invertedAlpha;

    private final int precision;

    public EMATemporalIndicator(TemporalIndicator<Decimal> base, int period, boolean allowUsingLastAvailablePrice, boolean allowExternalStartValue) {
        super(1000);
        this.base = base;
        this.period = period;
        this.alpha = Decimal.TWO.divide(new Decimal(period).add(Decimal.ONE));
        this.allowUsingLastAvailablePrice = allowUsingLastAvailablePrice;
        this.allowExternalStartValue = allowExternalStartValue;
        this.invertedAlpha = Decimal.ONE.subtract(alpha);
        this.precision = base.getBarSeries().getPair().precision();
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
            return base.getValue(index);
        }

        Decimal current = base.getValue(index);
        Decimal lastEma = getValue(getBarSeries().getLastTime(index));

        return alpha.multiply(current).add(invertedAlpha.multiply(lastEma)).round(precision);
    }

    @Override
    public int getUnstableBars() {
        return allowExternalStartValue ? 1 : period;
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return base.getBarSeries();
    }
}
