package com.becker.freelance.backtest;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.strategies.TradingStrategy;

import java.util.function.Supplier;

public record StrategyWithPair(Supplier<TradingStrategy> strategySupplier, Pair pair) {
}
