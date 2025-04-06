package com.becker.freelance.data;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.CompleteTimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Map;

public class BybitEurUsdRequestor implements EurUsdRequestor, DataSubscriber {

    private TimeSeries eurUsd;

    public BybitEurUsdRequestor() {
        SubscribableDataProvider dataProvider = new BybitSubscribableDataProvider(Pair.usdEur1());
        dataProvider.addSubscriber(this);
        eurUsd = new CompleteTimeSeries(Pair.eurUsd1(), Map.of());
    }

    @Override
    public TimeSeriesEntry getEurUsdForTime(LocalDateTime time) {
        return eurUsd.getEntryForTime(time);
    }

    @Override
    public void consume(TimeSeries timeSeries, LocalDateTime time) {
        this.eurUsd = timeSeries;
    }

    private TimeSeriesEntry transformUsdEurToEurUsd(TimeSeriesEntry entry) {
        Decimal one = Decimal.ONE;
        return new TimeSeriesEntry(entry.time(),
                one.divide(entry.openBid()), one.divide(entry.openAsk()),
                one.divide(entry.highBid()), one.divide(entry.highAsk()),
                one.divide(entry.lowBid()), one.divide(entry.lowAsk()),
                one.divide(entry.closeBid()), one.divide(entry.closeAsk()),
                entry.volume(), entry.trades(), Pair.eurUsd1());
    }
}
