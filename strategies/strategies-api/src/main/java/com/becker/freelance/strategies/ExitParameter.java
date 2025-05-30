package com.becker.freelance.strategies;

import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import org.ta4j.core.Bar;

import java.time.LocalDateTime;

public interface ExitParameter {

    TimeSeries timeSeries();

    LocalDateTime time();

    TimeSeriesEntry currentPrice();

    default Bar currentPriceAsBar() {
        return timeSeries().getEntryForTimeAsBar(time());
    }
}
