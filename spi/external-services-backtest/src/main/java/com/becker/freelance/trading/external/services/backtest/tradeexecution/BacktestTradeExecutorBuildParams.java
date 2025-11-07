package com.becker.freelance.trading.external.services.backtest.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;

public record BacktestTradeExecutorBuildParams(
        BacktestExecutionConfiguration backtestExecutionConfiguration,
        Pair pair,
        EurUsdRequestor eurUsdRequestor
) {
}
