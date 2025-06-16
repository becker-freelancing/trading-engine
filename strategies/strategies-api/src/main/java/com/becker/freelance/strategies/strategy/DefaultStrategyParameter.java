package com.becker.freelance.strategies.strategy;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.util.Set;

public record DefaultStrategyParameter(
        StrategyCreationParameter strategyParameter,
        Pair pair,
        Set<? extends TradeableQuantilMarketRegime> activeOnRegimes
) implements StrategyParameter {

    @Override
    public StrategyCreationParameter clone() {
        return StrategyParameter.super.clone();
    }
}
