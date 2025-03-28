package com.becker.freelance.capital.marketdata;

import com.becker.freelance.commons.pair.Pair;

public interface MarketDataListener {

    public void onMarketData(MarketData marketData);

    public Pair supportedPair();
}
