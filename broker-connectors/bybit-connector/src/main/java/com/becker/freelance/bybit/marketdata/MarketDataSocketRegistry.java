package com.becker.freelance.bybit.marketdata;

import com.becker.freelance.broker.marketdata.MarketData;
import com.becker.freelance.broker.marketdata.MarketDataListener;
import com.becker.freelance.commons.pair.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class MarketDataSocketRegistry {

    private static final Map<Pair, Set<Consumer<MarketData>>> listeners = new HashMap<>();
    private static MarketDataEndpoint marketDataEndpoint;

    public static synchronized void registerListener(Pair pair, MarketDataListener listener) {
        if (marketDataEndpoint != null) {
            marketDataEndpoint.stopListen();
        }
        listeners.computeIfAbsent(pair, p -> new HashSet<>());
        listeners.get(pair).add(listener);
        marketDataEndpoint = new MarketDataEndpoint(listeners);
        marketDataEndpoint.startListen();
    }

}
