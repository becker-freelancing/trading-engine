package com.becker.freelance.strategies.executionparameter;

import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;

public record DefaultExitExecutionParameter(TimeSeries timeSeries, LocalDateTime time,
                                            TimeSeriesEntry currentPrice) implements ExitExecutionParameter {
}
