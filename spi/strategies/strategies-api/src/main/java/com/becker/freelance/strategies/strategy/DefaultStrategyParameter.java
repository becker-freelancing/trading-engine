package com.becker.freelance.strategies.strategy;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.trading.external.services.strategies.StrategyCreationParameter;

import java.util.Set;

public record DefaultStrategyParameter(
        StrategyCreationParameter strategyParameter,
        Pair pair,
        Set<? extends TradeableMarketRegime> activeOnRegimes
) implements StrategyParameter {

    @Override
    public StrategyCreationParameter clone() {
        return StrategyParameter.super.clone();
    }
}
