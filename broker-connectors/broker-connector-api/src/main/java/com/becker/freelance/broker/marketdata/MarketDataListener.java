package com.becker.freelance.broker.marketdata;

import com.becker.freelance.commons.pair.Pair;

public interface MarketDataListener {

    public void onMarketData(MarketData marketData);

    public Pair supportedPair();
}
