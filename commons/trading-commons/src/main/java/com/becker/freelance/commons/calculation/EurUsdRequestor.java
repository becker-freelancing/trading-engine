package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;

public interface EurUsdRequestor {

    public TimeSeriesEntry getEurUsdForTime(LocalDateTime time);
}
