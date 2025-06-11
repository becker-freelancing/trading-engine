package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;

public interface PriceRequestor {

    public TimeSeriesEntry getPriceForTime(Pair pair, LocalDateTime time);
}
