package com.becker.freelance.strategies.executionparameter;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;

public interface StrategyExecutionParameter {

    TimeSeries timeSeries();

    LocalDateTime time();

    TimeSeriesEntry currentPrice();

    default Pair pair() {
        return timeSeries().getPair();
    }
}
