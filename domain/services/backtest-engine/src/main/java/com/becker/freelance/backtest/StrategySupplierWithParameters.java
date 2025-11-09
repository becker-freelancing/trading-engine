package com.becker.freelance.backtest;

import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.trading.external.services.strategies.StrategyCreationParameter;

public record StrategySupplierWithParameters(StrategySupplier strategySupplier, StrategyCreationParameter parameter) {
}
