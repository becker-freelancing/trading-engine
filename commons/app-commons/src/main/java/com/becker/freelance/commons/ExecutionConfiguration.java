package com.becker.freelance.commons;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.List;

public record ExecutionConfiguration(List<Pair> pairs, Decimal initialWalletAmount, TimeSeries eurUsd,
                                     LocalDateTime startTime,
                                     LocalDateTime endTime) {

    public TimeSeries getEurUsdTimeSeries() {
        return eurUsd;
    }
}
