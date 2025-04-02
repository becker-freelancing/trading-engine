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

    private static final Map<Pair, Set<Consumer<MarketData>>> derivateListeners = new HashMap<>();
    private static final Map<Pair, Set<Consumer<MarketData>>> spotListeners = new HashMap<>();
    private static MarketDataEndpoint marketDataEndpointDerivate;
    private static MarketDataEndpoint marketDataEndpointSpot;

    public static synchronized void registerListener(Pair pair, MarketDataListener listener) {
        if (pair.isUsdBaseCurrency() && pair.isEuroCounterCurrency()) {
            listenForSpot(pair, listener);
        } else {
            listenForDerivate(pair, listener);
        }
    }

    private static void listenForDerivate(Pair pair, MarketDataListener listener) {
        if (marketDataEndpointDerivate != null) {
            marketDataEndpointDerivate.stopListen();
        }
        derivateListeners.computeIfAbsent(pair, p -> new HashSet<>());
        derivateListeners.get(pair).add(listener);
        marketDataEndpointDerivate = MarketDataEndpoint.derivateEndpoint(derivateListeners);
        marketDataEndpointDerivate.startListen();
    }

    private static void listenForSpot(Pair pair, MarketDataListener listener) {
        if (marketDataEndpointSpot != null) {
            marketDataEndpointSpot.stopListen();
        }
        spotListeners.computeIfAbsent(pair, p -> new HashSet<>());
        spotListeners.get(pair).add(listener);
        marketDataEndpointSpot = MarketDataEndpoint.spotEndpoint(spotListeners);
        marketDataEndpointSpot.startListen();
    }

}
