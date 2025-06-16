package com.becker.freelance.app;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.strategy.DefaultStrategyParameter;
import com.becker.freelance.strategies.strategy.StrategyParameter;
import com.becker.freelance.strategies.strategy.TradingStrategy;
import com.becker.freelance.strategies.validinitparameter.ValidStrategyInitParameters;

import java.util.Set;

public record RegimeStrategyCreator(StrategyCreator baseCreator,
                                    Set<QuantileMarketRegime> regimes,
                                    int priority,
                                    Pair pair,
                                    StrategyParameter strategyCreationParameter) implements StrategyCreator {


    @Override
    public String strategyName() {
        return "regime_based_" + baseCreator.strategyName();
    }

    @Override
    public ValidStrategyInitParameters strategyParameters() {
        return baseCreator.strategyParameters();
    }

    public StrategyParameter strategyParameterForRegime(QuantileMarketRegime regime) {
        if (!regimes().contains(regime)) {
            throw new IllegalArgumentException("Regime " + regime + " not supported for strategy " + strategyName());
        }
        return new DefaultStrategyParameter(
                strategyCreationParameter(),
                pair,
                regimes());
    }

    @Override
    public TradingStrategy build(StrategyParameter strategyParameter) {
        if (!pair.equals(strategyParameter.pair())) {
            throw new IllegalStateException("Could not construct Strategy for pair: " + strategyParameter.pair().technicalName() + ". Expected Pair was " + pair().technicalName());
        }
        return baseCreator.build(strategyCreationParameter());
    }
}
