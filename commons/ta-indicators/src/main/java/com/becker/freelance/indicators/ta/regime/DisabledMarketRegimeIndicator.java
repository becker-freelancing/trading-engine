package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.regime.TradeableMarketRegime;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;

class DisabledMarketRegimeIndicator implements Indicator<TradeableMarketRegime> {

    private static final TradeableMarketRegime REGIME = new DisabledMarketRegime();

    private final BarSeries barSeries;

    DisabledMarketRegimeIndicator(BarSeries barSeries) {
        this.barSeries = barSeries;
    }

    @Override
    public TradeableMarketRegime getValue(int i) {
        return REGIME;
    }

    @Override
    public int getUnstableBars() {
        return 0;
    }

    @Override
    public BarSeries getBarSeries() {
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
