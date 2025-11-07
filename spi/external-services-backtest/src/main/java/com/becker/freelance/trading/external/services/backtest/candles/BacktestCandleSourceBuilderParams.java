package com.becker.freelance.trading.external.services.backtest.candles;

import com.becker.freelance.commons.pair.Pair;

public record BacktestCandleSourceBuilderParams(Pair pair, Synchronizer synchronizer) {
}
