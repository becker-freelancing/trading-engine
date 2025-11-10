package com.becker.freelance.engine;

import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.strategies.strategy.TradingStrategy;
import com.becker.freelance.trading.external.services.registry.ScopedExternalServiceRegistry;

public interface StrategySupplier {

    public TradingStrategy get(Pair pair, TradingCalculator tradingCalculator, ScopedExternalServiceRegistry scopedExternalServiceRegistry);
}
