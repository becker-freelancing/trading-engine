package com.becker.freelance.commons.position;


import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

public interface PositionFactory {

    public StopLimitPosition createStopLimitPosition(EntrySignal entrySignal);

    public TrailingPosition createTrailingPosition(EntrySignal entrySignal, TimeSeriesEntry currentPrice);

}
