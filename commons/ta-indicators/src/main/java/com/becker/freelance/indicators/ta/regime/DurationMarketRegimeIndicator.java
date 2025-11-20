package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;

import java.time.LocalDateTime;

public class DurationMarketRegimeIndicator extends CachableIndicator<LocalDateTime, DurationMarketRegimeImpl> implements TemporalIndicator<DurationMarketRegime> {

    private final TemporalIndicator<TradeableMarketRegime> marketRegimeIndicator;

    public DurationMarketRegimeIndicator(TemporalIndicator<TradeableMarketRegime> marketRegimeIndicator) {
        super(100);
        this.marketRegimeIndicator = marketRegimeIndicator;
    }

    @Override
    public DurationMarketRegime getValue(LocalDateTime index) {
        return getOrCompute(index);
    }

    @Override
    public int getUnstableBars() {
        return marketRegimeIndicator.getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return marketRegimeIndicator.getBarSeries();
    }

    @Override
    protected DurationMarketRegimeImpl computeMissing(LocalDateTime index) {
        TradeableMarketRegime currentRegime = marketRegimeIndicator.getValue(index);
        int duration = 1;
        LocalDateTime time = index;
        while (marketRegimeIndicator.getValue(time) != null) {
            if (!marketRegimeIndicator.getValue(time).equals(currentRegime)) {
                break;
            }
            duration++;
            time = time.minus(getBarSeries().getPairDuration());
        }

        return new DurationMarketRegimeImpl(currentRegime, duration);
    }
}
