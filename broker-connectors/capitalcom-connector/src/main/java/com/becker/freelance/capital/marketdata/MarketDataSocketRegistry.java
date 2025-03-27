package com.becker.freelance.capital.marketdata;

import com.becker.freelance.commons.pair.Pair;
import jakarta.websocket.DeploymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class MarketDataSocketRegistry {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataSocketRegistry.class);

    private static final Map<Pair, MarketDataSocket> SOCKETS = new HashMap<>();

    public static void registerListener(Pair pair, MarketDataListener listener) {
        SOCKETS.computeIfAbsent(pair, MarketDataSocket::new);
        SOCKETS.get(pair).addListener(listener);
        try {
            SOCKETS.get(pair).startListen();
        } catch (IOException | DeploymentException | URISyntaxException e) {
            logger.error("Could not start Listening on Market Data", e);
        }
    }
}
