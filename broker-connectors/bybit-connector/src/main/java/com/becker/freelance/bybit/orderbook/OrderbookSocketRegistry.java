package com.becker.freelance.bybit.orderbook;

import com.becker.freelance.broker.orderbook.OrderBookListener;
import com.becker.freelance.broker.orderbook.Orderbook;
import com.becker.freelance.commons.pair.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class OrderbookSocketRegistry {

    private static final Map<Pair, Set<Consumer<Orderbook>>> derivateListeners = new HashMap<>();
    private static final Map<Pair, Set<Consumer<Orderbook>>> spotListeners = new HashMap<>();
    private static OrderbookEndpoint marketDataEndpointDerivate;
    private static OrderbookEndpoint marketDataEndpointSpot;

    public static synchronized void registerListener(Pair pair, OrderBookListener listener) {
        if (pair.isUsdBaseCurrency() && pair.isEuroCounterCurrency()) {
            listenForSpot(pair, listener);
        } else {
            listenForDerivate(pair, listener);
        }
    }

    private static void listenForDerivate(Pair pair, OrderBookListener listener) {
        if (marketDataEndpointDerivate != null) {
            marketDataEndpointDerivate.stopListen();
        }
        derivateListeners.computeIfAbsent(pair, p -> new HashSet<>());
        derivateListeners.get(pair).add(listener);
        marketDataEndpointDerivate = OrderbookEndpoint.derivateEndpoint(derivateListeners);
        marketDataEndpointDerivate.startListen();
    }

    private static void listenForSpot(Pair pair, OrderBookListener listener) {
        if (marketDataEndpointSpot != null) {
            marketDataEndpointSpot.stopListen();
        }
        spotListeners.computeIfAbsent(pair, p -> new HashSet<>());
        spotListeners.get(pair).add(listener);
        marketDataEndpointSpot = OrderbookEndpoint.spotEndpoint(spotListeners);
        marketDataEndpointSpot.startListen();
    }

}
