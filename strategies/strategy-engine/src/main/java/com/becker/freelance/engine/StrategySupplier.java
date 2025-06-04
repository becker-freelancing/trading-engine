package com.becker.freelance.engine;

import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.strategies.strategy.TradingStrategy;

public interface StrategySupplier {

    public TradingStrategy get(Pair pair, TradingCalculator tradingCalculator);
}
