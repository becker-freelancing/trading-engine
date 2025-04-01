package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;

public record TimeSeriesEurUsdRequestor(TimeSeries timeSeries) implements EurUsdRequestor {
    @Override
    public TimeSeriesEntry getEurUsdForTime(LocalDateTime time) {
        return timeSeries.getEntryForTime(time);
    }
}
