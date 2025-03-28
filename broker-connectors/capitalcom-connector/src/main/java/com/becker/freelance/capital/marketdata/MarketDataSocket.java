package com.becker.freelance.capital.marketdata;

import com.becker.freelance.capital.env.ConversationContext;
import com.becker.freelance.capital.env.ConversationContextHolder;
import com.becker.freelance.commons.pair.Pair;
import jakarta.websocket.DeploymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class MarketDataSocket {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataSocket.class);

    private final Map<Pair, Map<LocalDateTime, BidMarketData>> bidMarketDataCache;
    private final Map<Pair, Map<LocalDateTime, AskMarketData>> askMarketDataCache;
    private final Map<Pair, Set<MarketDataListener>> marketDataListeners;
    private final MarketDataEndpoint marketDataEndpoint;
    private final Set<Pair> pair;
    private boolean isListening;

    public MarketDataSocket(Set<Pair> pair, Map<Pair, Set<MarketDataListener>> listeners) {
        this.pair = pair;
        this.bidMarketDataCache = new HashMap<>();
        this.askMarketDataCache = new HashMap<>();
        marketDataListeners = listeners;
        pair.forEach(p -> {
            bidMarketDataCache.put(p, new HashMap<>());
            askMarketDataCache.put(p, new HashMap<>());
            marketDataListeners.computeIfAbsent(p, pa -> new HashSet<>());
        });
        marketDataEndpoint = new MarketDataEndpoint(this::onBidData, this::onAskData, this::onClose, this::onOpen);
        isListening = false;
    }

    private void onOpen() {
        isListening = true;
    }

    private void onClose() {
        isListening = false;

        logger.info("Trying to reconnect...");
        while (!isListening) {
            try {
                startListen();
            } catch (IOException | DeploymentException | URISyntaxException e) {
                logger.warn("Exception in trying to start listen", e);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Could not wait to reconnect. Interrupted", e);
            }
        }
    }

    public void startListen() throws IOException, DeploymentException, URISyntaxException {
        if (isListening) {
            return;
        }
        ConversationContext conversationContext = ConversationContextHolder.getConversationContext();
        marketDataEndpoint.subscribeOHLC(conversationContext, pair);
    }


    public void stopListening() throws IOException {
        if (!isListening) {
            return;
        }
        ConversationContext conversationContext = ConversationContextHolder.getConversationContext();
        marketDataEndpoint.unsubscribeOHLC(conversationContext, pair);
    }

    public void addListener(MarketDataListener listener) {
        marketDataListeners.get(listener.supportedPair()).add(listener);
    }

    private void onBidData(BidMarketData bidMarketData) {
        LocalDateTime closeTime = bidMarketData.closeTime();
        synchronized (askMarketDataCache) {
            if (askMarketDataCache.get(bidMarketData.pair()).containsKey(closeTime)) {
                AskMarketData askMarketData = askMarketDataCache.get(bidMarketData.pair()).get(closeTime);
                notifyListener(askMarketData, bidMarketData, closeTime);
                askMarketDataCache.get(askMarketData.pair()).remove(closeTime);
            } else {
                bidMarketDataCache.get(bidMarketData.pair()).put(closeTime, bidMarketData);
            }
        }
    }

    private void onAskData(AskMarketData askMarketData) {
        LocalDateTime closeTime = askMarketData.closeTime();
        synchronized (bidMarketDataCache) {
            if (bidMarketDataCache.get(askMarketData.pair()).containsKey(closeTime)) {
                BidMarketData bidMarketData = bidMarketDataCache.get(askMarketData.pair()).get(closeTime);
                notifyListener(askMarketData, bidMarketData, closeTime);
                bidMarketDataCache.get(askMarketData.pair()).remove(closeTime);
            } else {
                askMarketDataCache.get(askMarketData.pair()).put(closeTime, askMarketData);
            }
        }
    }

    private void notifyListener(AskMarketData askMarketData, BidMarketData bidMarketData, LocalDateTime closeTime) {
        MarketData marketData = new MarketData(askMarketData.pair(), closeTime,
                bidMarketData.openBid(), askMarketData.openAsk(),
                bidMarketData.highBid(), askMarketData.highAsk(),
                bidMarketData.lowBid(), askMarketData.lowAsk(),
                bidMarketData.closeBid(), askMarketData.closeAsk());

        marketDataListeners.get(marketData.pair()).forEach(consumer -> consumer.onMarketData(marketData));
    }

    public Set<Pair> pairs() {
        return pair;
    }

    public Map<Pair, Set<MarketDataListener>> listener() {
        return marketDataListeners;
    }
}
