package com.becker.freelance.data;

import com.becker.freelance.capital.marketdata.MarketData;
import com.becker.freelance.capital.marketdata.MarketDataListener;
import com.becker.freelance.capital.marketdata.MarketDataSocketRegistry;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.CompleteTimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CapitalSubscribableDataProvider extends SubscribableDataProvider implements MarketDataListener {

    private static final AsyncValueHolder<Pair, TimeSeries> TIME_SERIES_HOLDER = new AsyncValueHolder<>();

    private final Set<DataSubscriber> subscribers;
    private final Pair pair;


    public CapitalSubscribableDataProvider(Pair pair) {
        this.pair = pair;
        this.subscribers = new LinkedHashSet<>();
        MarketDataSocketRegistry.registerListener(pair, this);
    }

    @Override
    public void addSubscriber(DataSubscriber subscriber) {
        subscribers.add(subscriber);
    }


    @Override
    public void synchronize(LocalDateTime time) {

    }


    @Override
    public void onMarketData(MarketData marketData) {
        TimeSeriesEntry entry = map(marketData);
        TimeSeries timeSeries = TIME_SERIES_HOLDER.getOrRead(pair, this::createNewTimeSeries);
        timeSeries.addEntry(entry);
        System.out.println(entry);
        subscribers.forEach(subscribers -> subscribers.consume(timeSeries, entry.time()));
    }

    @Override
    public Pair supportedPair() {
        return pair;
    }

    @Override
    public TimeSeries getCurrentTimeSeries() {
        return TIME_SERIES_HOLDER.getOrRead(pair, this::createNewTimeSeries);
    }

    private TimeSeries createNewTimeSeries() {
        return new CompleteTimeSeries(pair, Map.of());
    }

    private TimeSeriesEntry map(MarketData marketData) {
        return new TimeSeriesEntry(marketData.closeTime(),
                marketData.openBid(), marketData.openAsk(),
                marketData.highBid(), marketData.highAsk(),
                marketData.lowBid(), marketData.lowAsk(),
                marketData.closeBid(), marketData.closeAsk(),
                Decimal.ZERO, Decimal.ZERO, marketData.pair());
    }

}
