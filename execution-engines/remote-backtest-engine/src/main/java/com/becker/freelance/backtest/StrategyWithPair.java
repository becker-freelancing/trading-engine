package com.becker.freelance.backtest;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.engine.StrategySupplier;

public record StrategyWithPair(StrategySupplier strategySupplier, Pair pair) {
}
