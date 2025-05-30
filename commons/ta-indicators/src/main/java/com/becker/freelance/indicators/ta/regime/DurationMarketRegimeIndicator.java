package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;

import java.util.Optional;

public class DurationMarketRegimeIndicator extends CachableIndicator<Integer, DurationMarketRegime> implements Indicator<DurationMarketRegime> {

    private final Indicator<MarketRegime> marketRegimeIndicator;

    public DurationMarketRegimeIndicator(Indicator<MarketRegime> marketRegimeIndicator) {
        super(100);
        this.marketRegimeIndicator = marketRegimeIndicator;
    }

    @Override
    public DurationMarketRegime getValue(int index) {
        Optional<DurationMarketRegime> inCache = findInCache(index);
        if (inCache.isPresent()) {
            return inCache.get();
        }

        MarketRegime currentRegime = marketRegimeIndicator.getValue(index);
        int duration = 1;
        for (int i = index - 1; i >= getUnstableBars(); i--) {
            if (!marketRegimeIndicator.getValue(i).equals(currentRegime)) {
                break;
            }
            duration += 1;
        }

        DurationMarketRegime durationMarketRegime = new DurationMarketRegime(currentRegime, duration);
        putInCache(index, durationMarketRegime);
        return durationMarketRegime;
    }

    @Override
    public int getUnstableBars() {
        return marketRegimeIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return marketRegimeIndicator.getBarSeries();
    }
}
