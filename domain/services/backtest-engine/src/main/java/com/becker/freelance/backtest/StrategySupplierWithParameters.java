package com.becker.freelance.backtest;

import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;

public record StrategySupplierWithParameters(StrategySupplier strategySupplier, StrategyCreationParameter parameter) {
}
