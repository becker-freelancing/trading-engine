package com.becker.freelance.trading.external.services.backtest.candles;

import com.becker.freelance.trading.external.services.candles.CandleDataSource;

public interface BacktestCandleDataSource extends CandleDataSource {

    public void reset();
}
