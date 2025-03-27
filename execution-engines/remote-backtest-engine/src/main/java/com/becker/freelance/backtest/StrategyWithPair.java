package com.becker.freelance.backtest;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.strategies.BaseStrategy;

import java.util.function.Supplier;

public record StrategyWithPair(Supplier<BaseStrategy> strategySupplier, Pair pair) {
}
