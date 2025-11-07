package com.becker.freelance.trading.external.services.backtest.candles;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.QueueTimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.trading.external.services.candles.DataSubscriber;

import java.time.LocalDateTime;

public class DefaultEurUsdRequestor implements EurUsdRequestor, DataSubscriber {

    private TimeSeries eurUsd;

    private DefaultEurUsdRequestor() {
        eurUsd = new QueueTimeSeries(Pair.eurUsd1(), 100);
    }


    public DefaultEurUsdRequestor(BacktestCandleDataSource eurUsdDataSource) {
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
