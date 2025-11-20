package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.indicators.ta.temporal.indicator.TemporalIndicator;
import com.becker.freelance.indicators.ta.temporal.series.TemporalBarSeries;

import java.time.LocalDateTime;

class DisabledMarketRegimeIndicator implements TemporalIndicator<TradeableMarketRegime> {

    private static final TradeableMarketRegime REGIME = new DisabledMarketRegime();

    private final TemporalBarSeries barSeries;

    public DisabledMarketRegimeIndicator(TemporalBarSeries barSeries) {
        this.barSeries = barSeries;
    }

    @Override
    public TradeableMarketRegime getValue(LocalDateTime i) {
        return REGIME;
    }

    @Override
    public int getUnstableBars() {
        return 0;
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return barSeries;
    }

    private static final class DisabledMarketRegime implements TradeableMarketRegime {
        @Override
        public String name() {
            return "DISABLED_BY_CONFIG";
        }

        @Override
        public int id() {
            return 0;
        }

        @Override
        public boolean considersRegimeDuration() {
            return false;
        }
    }
}
