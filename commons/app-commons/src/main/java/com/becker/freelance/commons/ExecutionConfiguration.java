package com.becker.freelance.commons;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;

import java.time.LocalDateTime;

public record ExecutionConfiguration(Pair pair, double initialWalletAmount, TimeSeries eurUsd, LocalDateTime startTime,
                                     LocalDateTime endTime) {

    public TimeSeries getEurUsdTimeSeries() {
        return eurUsd;
    }
}
