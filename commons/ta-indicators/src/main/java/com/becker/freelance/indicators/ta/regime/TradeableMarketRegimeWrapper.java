package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.regime.TradeableMarketRegime;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface TradeableMarketRegimeWrapper {

    public static Set<TradeableMarketRegime> all() {
        return Stream.of(
                        MarketRegime.values(),
                        QuantileMarketRegime.values()
                ).flatMap(Arrays::stream)
                .collect(Collectors.toSet());
    }


}
