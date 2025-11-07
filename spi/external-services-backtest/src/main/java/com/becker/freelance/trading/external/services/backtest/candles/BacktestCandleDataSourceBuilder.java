package com.becker.freelance.trading.external.services.backtest.candles;

import com.becker.freelance.trading.external.services.candles.CandleDataSourceBuilder;

public interface BacktestCandleDataSourceBuilder extends CandleDataSourceBuilder<BacktestCandleSourceBuilderParams, BacktestCandleDataSource> {


    @Override
    default Class<? extends BacktestCandleDataSource> getServiceClass() {
        return BacktestCandleDataSource.class;
    }
}
