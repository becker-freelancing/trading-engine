package com.becker.freelance.trading.external.services.backtest.candles;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.trading.external.services.candles.CandleDataSourceBuilder;

public interface BacktestCandleDataSourceBuilder extends CandleDataSourceBuilder<BacktestCandleSourceBuilderParams, BacktestCandleDataSource> {


    public EurUsdRequestor createEuroUsdRequestor(Synchronizer synchronizer);

    @Override
    default Class<? extends BacktestCandleDataSource> getServiceClass() {
        return BacktestCandleDataSource.class;
    }
}
