package com.becker.freelance.broker.marketdata;

import com.becker.freelance.commons.pair.Pair;

import java.util.function.Consumer;

public interface MarketDataListener extends Consumer<MarketData> {

    public void onMarketData(MarketData marketData);

    public Pair supportedPair();

    @Override
    default void accept(MarketData marketData) {
        onMarketData(marketData);
    }
}
