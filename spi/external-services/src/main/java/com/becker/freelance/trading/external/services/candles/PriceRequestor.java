package com.becker.freelance.trading.external.services.candles;

import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRequestor {

    public TimeSeriesEntry getPriceForTime(LocalDateTime time);

    public List<TimeSeriesEntry> getPriceInRange(LocalDateTime from, LocalDateTime to);
}
