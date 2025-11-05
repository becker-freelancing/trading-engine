package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.regime.TradeableMarketRegime;

public interface DurationMarketRegime extends TradeableMarketRegime {

    TradeableMarketRegime marketRegime();
    int duration();
}
