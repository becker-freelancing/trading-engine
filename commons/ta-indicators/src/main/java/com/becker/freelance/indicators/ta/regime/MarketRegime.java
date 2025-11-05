package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.regime.TradeableMarketRegime;

public enum MarketRegime implements TradeableMarketRegime {
    DOWN_HIGH_VOLA(1),
    DOWN_LOW_VOLA(2),
    SIDE_HIGH_VOLA(3),
    SIDE_LOW_VOLA(4),
    UP_HIGH_VOLA(5),
    UP_LOW_VOLA(6);

    private final int id;

    MarketRegime(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public boolean considersRegimeDuration() {
        return true;
    }
}
