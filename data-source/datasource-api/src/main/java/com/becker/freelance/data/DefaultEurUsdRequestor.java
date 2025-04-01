package com.becker.freelance.data;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.CompleteTimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;
import java.util.Map;

class DefaultEurUsdRequestor implements EurUsdRequestor, DataSubscriber {

    private TimeSeries eurUsd;

    private DefaultEurUsdRequestor() {
        eurUsd = new CompleteTimeSeries(Pair.eurUsd1(), Map.of());
    }

    public DefaultEurUsdRequestor(DataProviderFactory factory) {
        this();
        SubscribableDataProvider dataProvider = factory.createSubscribableDataProvider(Pair.eurUsd1());
        dataProvider.addSubscriber(this);
    }

    public DefaultEurUsdRequestor(DataProviderFactory factory, Synchronizer synchronizer) {
        this();
        SubscribableDataProvider dataProvider = factory.createSubscribableDataProvider(Pair.eurUsd1(), synchronizer);
        dataProvider.addSubscriber(this);
    }

    @Override
    public TimeSeriesEntry getEurUsdForTime(LocalDateTime time) {
        return eurUsd.getEntryForTime(time);
    }

    @Override
    public void consume(TimeSeries timeSeries, LocalDateTime time) {
        this.eurUsd = timeSeries;
    }
}
