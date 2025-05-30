package com.becker.freelance.strategies;

import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;

public record DefaultExitParameter(TimeSeries timeSeries, LocalDateTime time,
                                   TimeSeriesEntry currentPrice) implements ExitParameter {
}
