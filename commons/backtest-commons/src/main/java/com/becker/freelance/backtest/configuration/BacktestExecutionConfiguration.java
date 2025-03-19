package com.becker.freelance.backtest.configuration;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.List;

public record BacktestExecutionConfiguration(List<Pair> pairs,
                                             Decimal initialWalletAmount,
                                             TimeSeries eurUsd,
                                             LocalDateTime startTime,
                                             LocalDateTime endTime,
                                             Integer numberOfThreads) {

    public TimeSeries getEurUsdTimeSeries() {
        return eurUsd;
    }
}
