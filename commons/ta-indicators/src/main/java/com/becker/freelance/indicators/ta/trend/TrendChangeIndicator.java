package com.becker.freelance.indicators.ta.trend;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;

import java.util.Optional;

public class TrendChangeIndicator implements Indicator<Optional<TrendChange>> {

    private final Indicator<Trend> trendIndicator;

    public TrendChangeIndicator(Indicator<Trend> trendIndicator) {
        this.trendIndicator = trendIndicator;
    }

    @Override
    public Optional<TrendChange> getValue(int index) {
        if (index == 0) {
            return Optional.empty();
        }

        Trend lastTrend = trendIndicator.getValue(index - 1);
        Trend currentTrend = trendIndicator.getValue(index);

        if (lastTrend.direction() != currentTrend.direction()) {
            return Optional.of(new TrendChange(lastTrend, currentTrend));
        }

        return Optional.empty();
    }

    @Override
    public int getUnstableBars() {
        return trendIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return trendIndicator.getBarSeries();
    }
}
