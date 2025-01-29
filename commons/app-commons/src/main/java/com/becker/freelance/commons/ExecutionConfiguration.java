package com.becker.freelance.commons;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;

import java.time.LocalDateTime;
import com.becker.freelance.math.Decimal;

public record ExecutionConfiguration(Pair pair, Decimal initialWalletAmount, TimeSeries eurUsd, LocalDateTime startTime,
                                     LocalDateTime endTime) {

    public TimeSeries getEurUsdTimeSeries() {
        return eurUsd;
    }
}
