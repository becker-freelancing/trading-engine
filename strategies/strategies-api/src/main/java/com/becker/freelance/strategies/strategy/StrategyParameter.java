package com.becker.freelance.strategies.strategy;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.ParameterName;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.util.Map;
import java.util.Set;

public interface StrategyParameter extends StrategyCreationParameter {

    public StrategyCreationParameter strategyParameter();

    public Pair pair();

    public Set<? extends TradeableQuantilMarketRegime> activeOnRegimes();

    @Override
    default void addParameter(ParameterName name, Decimal value) {
        strategyParameter().addParameter(name, value);
    }

    @Override
    default Decimal getParameter(ParameterName parameterName) {
        return strategyParameter().getParameter(parameterName);
    }

    @Override
    default boolean getParameterAsBool(ParameterName parameterName) {
        return strategyParameter().getParameterAsBool(parameterName);
    }

    @Override
    default int getParameterAsInt(ParameterName parameterName) {
        return strategyParameter().getParameterAsInt(parameterName);
    }

    @Override
    default double getParameterAsDouble(ParameterName parameterName) {
        return strategyParameter().getParameterAsDouble(parameterName);
    }

    @Override
    default Map<String, Decimal> asMap() {
        return strategyParameter().asMap();
    }

    @Override
    default StrategyCreationParameter clone() {
        return strategyParameter().clone();
    }
}
