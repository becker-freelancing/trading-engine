package com.becker.freelance.trading.external.services.backtest.candles;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.QueueTimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.trading.external.services.candles.DataSubscriber;

import java.time.LocalDateTime;

class BacktestEurUsdRequestor implements EurUsdRequestor, DataSubscriber {

    private TimeSeries eurUsd;

    private BacktestEurUsdRequestor() {
        eurUsd = new QueueTimeSeries(Pair.eurUsd1(), 100);
    }


    public BacktestEurUsdRequestor(BacktestCandleDataSource eurUsdDataSource) {
        this();
        eurUsdDataSource.addSubscriber(this);
    }

    @Override
    public TimeSeriesEntry getEurUsdForTime(LocalDateTime time) {
        return eurUsd.getEntryForTime(time);
    }

    @Override
    public void consume(TimeSeries timeSeries, LocalDateTime time) {
        this.eurUsd = timeSeries;
    }
}
