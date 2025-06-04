package com.becker.freelance.strategies.strategy;

import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;
import com.becker.freelance.strategies.creation.StrategyCreator;

public record DefaultStrategyParameter(
        StrategyCreationParameter strategyParameter,
        TradingCalculator tradingCalculator,
        Pair pair,
        StrategyCreator strategyCreator
) implements StrategyParameter {

    @Override
    public StrategyCreationParameter clone() {
        return StrategyParameter.super.clone();
    }
}
