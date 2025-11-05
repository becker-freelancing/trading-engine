package com.becker.freelance.execution;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.engine.StrategySupplier;

public record StrategyWithPair(StrategySupplier strategySupplier, Pair pair) {
}
