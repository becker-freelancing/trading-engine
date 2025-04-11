package com.becker.freelance.bybit.orderbook;

import com.becker.freelance.broker.orderbook.OrderBookListener;
import com.becker.freelance.broker.orderbook.Orderbook;
import com.becker.freelance.bybit.util.PairConverter;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;
import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.websocket.WebsocketMessageHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class OrderbookEndpoint {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(OrderbookEndpoint.class);

    private final String urlPath;
    private final Set<Pair> pairs;
    private final Map<Pair, Set<OrderBookListener>> marketDataConsumer;
    private final String endpoint;
    private WebSocketClientDelegate websocketClient;
    private OrderBookEndpointWatcher endpointWatcher;


    private OrderbookEndpoint(Map<Pair, Set<OrderBookListener>> marketDataConsumer, String urlPath) {
        this.pairs = marketDataConsumer.keySet();
        this.marketDataConsumer = marketDataConsumer;
        this.urlPath = urlPath;
        this.endpoint = BybitApiConfig.STREAM_MAINNET_DOMAIN;
        setWebsocketClient();
    }

    public static OrderbookEndpoint derivateEndpoint(Map<Pair, Set<OrderBookListener>> derivateListeners) {
        return new OrderbookEndpoint(derivateListeners, BybitApiConfig.V5_PUBLIC_LINEAR);
    }

    public static OrderbookEndpoint spotEndpoint(Map<Pair, Set<OrderBookListener>> spotListeners) {
        return new OrderbookEndpoint(spotListeners, BybitApiConfig.V5_PUBLIC_SPOT);
    }

    private void onError() {
        endpointWatcher.stopWatching();
        setWebsocketClient();
        startListen();
    }

    private void setWebsocketClient() {
        SocketMessageHandler messageHandler = new SocketMessageHandler(marketDataConsumer);
        endpointWatcher = new OrderBookEndpointWatcher(messageHandler::getLastUpdateTime);
        websocketClient = new WebSocketClientDelegate(endpoint, false, messageHandler, this::onError);
    }

    public void startListen() {
        List<String> bybitPairs = pairs.stream().map(this::convertPairs).distinct().toList();
        websocketClient.getPublicChannelStream(bybitPairs, urlPath);
        endpointWatcher.watchUpdateTime();
    }

    public void stopListen() {
        websocketClient.disconnect();
        endpointWatcher.stopWatching();
    }

    private String convertPairs(Pair pair) {
        PairConverter pairConverter = new PairConverter();
        return "orderbook.200." + pairConverter.convert(pair);
    }

    private static class SocketMessageHandler implements WebsocketMessageHandler {

        private final Map<Pair, Set<OrderBookListener>> orderBookConsumers;
        private final PairConverter pairConverter;
        private LocalDateTime lastUpdateTime;

        private SocketMessageHandler(Map<Pair, Set<OrderBookListener>> orderBookConsumers) {
            this.orderBookConsumers = orderBookConsumers;
            this.pairConverter = new PairConverter();
            this.lastUpdateTime = LocalDateTime.MIN;
        }


        @Override
        public void handleMessage(String message) {
            if (!message.contains("topic")) {
                return;
            }
            try {
                OrderBookResponse orderBookResponse = objectMapper.readValue(message, OrderBookResponse.class);
                map(orderBookResponse);
            } catch (JsonProcessingException e) {
                logger.error("Could not convert to MarketData: {}", message, e);
            }
        }

        private void map(OrderBookResponse orderBookResponse) {
            String[] epic = orderBookResponse.getTopic().split("\\.");
            Optional<Pair> convert = pairConverter.convert(epic[2], "1");

            if (convert.isEmpty()) {
                return;
            }

            List<List<String>> bids = orderBookResponse.getData().getB();
            List<Decimal> bidValues = bids.stream().map(l -> l.get(0)).map(Decimal::new).toList();
            List<Decimal> bidQuantities = bids.stream().map(l -> l.get(1)).map(Decimal::new).toList();
            List<List<String>> asks = orderBookResponse.getData().getA();
            List<Decimal> asksValues = asks.stream().map(l -> l.get(0)).map(Decimal::new).toList();
            List<Decimal> asksQuantities = asks.stream().map(l -> l.get(1)).map(Decimal::new).toList();
            LocalDateTime time = map(orderBookResponse.getTs());
            setLastUpdateTime(time);
            Orderbook create = new Orderbook(convert.get(), time, orderBookResponse.getType(),
                    bidValues, bidQuantities, asksValues, asksQuantities);
            orderBookConsumers.getOrDefault(convert.get(), Set.of()).forEach(consumer -> consumer.accept(create));
        }

        private synchronized LocalDateTime getLastUpdateTime() {
            return lastUpdateTime;
        }

        private synchronized void setLastUpdateTime(LocalDateTime lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }

        private LocalDateTime map(long start) {
            return Instant.ofEpochMilli(start)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
    }
}
