package com.becker.freelance.trading.external.services.backtest.tradeexecution;

import com.becker.freelance.trading.external.services.tradeexecution.TradeExecutorBuilder;

public interface BacktestTradeExecutorBuilder extends TradeExecutorBuilder<BacktestTradeExecutorBuildParams, BacktestTradeExecutor> {

    @Override
    default Class<? extends BacktestTradeExecutor> getServiceClass() {
        return BacktestTradeExecutor.class;
    }
}
