package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.regime.TradeableMarketRegime;

record DurationMarketRegimeImpl(TradeableMarketRegime marketRegime, int duration) implements DurationMarketRegime{
    @Override
    public String name() {
        return marketRegime.name() + "_duration_" + duration;
    }

    @Override
    public int id() {
        return Integer.parseInt(marketRegime.id() + String.valueOf(duration));
    }

    @Override
    public boolean considersRegimeDuration() {
        return true;
    }
}
