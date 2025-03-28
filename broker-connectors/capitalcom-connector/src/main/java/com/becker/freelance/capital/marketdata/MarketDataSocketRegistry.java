package com.becker.freelance.capital.marketdata;

import com.becker.freelance.commons.pair.Pair;
import jakarta.websocket.DeploymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MarketDataSocketRegistry {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataSocketRegistry.class);

    private static MarketDataSocket SOCKET = null;

    public static synchronized void registerListener(Pair pair, MarketDataListener listener) {
        stopListening();
        Set<Pair> listenedPairs = getListenedPairs();
        listenedPairs.add(pair);
        Map<Pair, Set<MarketDataListener>> listeners = getListeners();
        listeners.computeIfAbsent(pair, p -> new HashSet<>());
        listeners.get(pair).add(listener);
        startListen(listenedPairs, listeners);
    }

    private static Map<Pair, Set<MarketDataListener>> getListeners() {
        if (SOCKET == null) {
            return new HashMap<>();
        }
        return new HashMap<>(SOCKET.listener());

    }

    private static void startListen(Set<Pair> pairsToListen, Map<Pair, Set<MarketDataListener>> listeners) {
        SOCKET = new MarketDataSocket(pairsToListen, listeners);
        try {
            SOCKET.startListen();
        } catch (IOException | DeploymentException | URISyntaxException e) {
            logger.error("Could not start Listening on Market Data", e);
        }
    }

    private static void stopListening() {
        if (SOCKET == null) {
            return;
        }
        try {
            SOCKET.stopListening();
        } catch (IOException e) {
            logger.error("Could not stop Listening on Market Data", e);
        }
    }

    private static Set<Pair> getListenedPairs() {
        if (SOCKET == null) {
            return new HashSet<>();
        }

        return new HashSet<>(SOCKET.pairs());
    }
}
