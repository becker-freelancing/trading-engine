package com.becker.freelance.trading.external.services.tradeexecution;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.trading.external.services.broker.ClosedTradesRequestor;
import com.becker.freelance.trading.external.services.broker.OpenPositionRequestor;
import com.becker.freelance.trading.external.services.registry.ExternalService;

import java.time.LocalDateTime;

public interface TradeExecutor extends ExternalService, OpenPositionRequestor, ClosedTradesRequestor {

    void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal);

    void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal);

    void adaptPositions(TimeSeriesEntry currentPrice);

    void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice);
}
