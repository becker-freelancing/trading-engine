package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QuantilesMarketRegimeIndicator extends CachableIndicator<LocalDateTime, QuantileMarketRegime> implements TemporalIndicator<TradeableMarketRegime> {

    private final TemporalIndicator<DurationMarketRegime> regimeIndicator;
    private final Map<MarketRegime, List<Double>> quantiles;

    public QuantilesMarketRegimeIndicator(TemporalIndicator<DurationMarketRegime> regimeIndicator, Map<MarketRegime, List<Double>> quantiles) {
        super(1000);
        this.regimeIndicator = regimeIndicator;
        this.quantiles = quantiles;
    }

    @Override
    public QuantileMarketRegime getValue(LocalDateTime index) {
        Optional<QuantileMarketRegime> inCache = findInCache(index);
        if (inCache.isPresent()) {
            return inCache.get();
        }

        DurationMarketRegime durationMarketRegime = regimeIndicator.getValue(index);
        MarketRegime marketRegime = (MarketRegime) durationMarketRegime.marketRegime();
        List<Double> quantiles = this.quantiles.get(marketRegime);
        int duration = durationMarketRegime.duration();
        QuantileMarketRegime quantileMarketRegime = QuantileMarketRegime.maxQuantile(marketRegime);
        for (int i = 0; i < quantiles.size(); i++) {
            Double quantileValue = quantiles.get(i);
            if (quantileValue > duration) {
                quantileMarketRegime = map(marketRegime, i);
                break;
            }
        }
        putInCache(index, quantileMarketRegime);
        return quantileMarketRegime;
    }

    private QuantileMarketRegime map(MarketRegime marketRegime, int i) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("Index i must be 0, 1, or 2");
        }

        String suffix = switch (i) {
            case 0 -> "_033";
            case 1 -> "_066";
            default -> "_1";
        };

        String enumName = marketRegime.name() + suffix;
        try {
            return QuantileMarketRegime.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Could not find QuantileMarketRegime with name " + enumName);
        }
    }

    @Override
    public int getUnstableBars() {
        return regimeIndicator.getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return regimeIndicator.getBarSeries();
    }
}
