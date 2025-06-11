package com.becker.freelance.data;

import com.becker.freelance.broker.marketdata.MarketData;
import com.becker.freelance.broker.marketdata.MarketDataListener;
import com.becker.freelance.bybit.marketdata.MarketDataSocketRegistry;
import com.becker.freelance.bybit.orderbook.OrderbookSocketRegistry;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.CompleteTimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BybitSubscribableDataProvider extends SubscribableDataProvider implements MarketDataListener {

    private static final Logger logger = LoggerFactory.getLogger(BybitSubscribableDataProvider.class);

    private static final AsyncValueHolder<Pair, TimeSeries> TIME_SERIES_HOLDER = new AsyncValueHolder<>();

    private final Set<DataSubscriber> subscribers;
    private final Pair pair;
    private LocalDateTime lastAddedTime = LocalDateTime.MIN;


    public BybitSubscribableDataProvider(Pair pair) {
        this.pair = pair;
        this.subscribers = new LinkedHashSet<>();
        MarketDataSocketRegistry.registerListener(pair, this);
        OrderbookSocketRegistry.registerListener(pair, new OrderBookListener(pair));
    }

    @Override
    public void addSubscriber(DataSubscriber subscriber) {
        subscribers.add(subscriber);
    }


    @Override
    public void synchronize(LocalDateTime time) {

    }


    @Override
    public synchronized void onMarketData(MarketData marketData) {
        if (marketData.closeTime().equals(lastAddedTime)) {
            return;
        }
        logger.debug("Receiving Market-Data: {}", marketData);
        lastAddedTime = marketData.closeTime();
        TimeSeriesEntry entry = map(marketData);
        TimeSeries timeSeries = TIME_SERIES_HOLDER.getOrRead(pair, this::createNewTimeSeries);
        timeSeries.addEntry(entry);
        subscribers.forEach(subscribers -> subscribers.consume(timeSeries, entry.time()));
    }

    @Override
    public Pair supportedPair() {
        return pair;
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
                marketData.volume(), Decimal.ZERO, marketData.pair());
    }

    @Override
    public TimeSeriesEntry getPriceForTime(Pair pair, LocalDateTime time) {

        throw new UnsupportedOperationException("Not implemented yet");
    }
}
