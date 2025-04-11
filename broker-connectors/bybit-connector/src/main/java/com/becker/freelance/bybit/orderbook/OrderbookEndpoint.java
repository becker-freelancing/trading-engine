package com.becker.freelance.bybit.orderbook;

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
import java.util.*;
import java.util.function.Consumer;

class OrderbookEndpoint {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(OrderbookEndpoint.class);

    private final String urlPath;
    private final Set<Pair> pairs;
    private final Map<Pair, Set<Consumer<Orderbook>>> marketDataConsumer;
    private final String endpoint;
    private WebSocketClientDelegate websocketClient;

    private OrderbookEndpoint(Map<Pair, Set<Consumer<Orderbook>>> marketDataConsumer, String urlPath) {
        this.pairs = marketDataConsumer.keySet();
        this.marketDataConsumer = marketDataConsumer;
        this.urlPath = urlPath;
        String endpoint = BybitApiConfig.STREAM_MAINNET_DOMAIN;
        this.endpoint = endpoint;
        setWebsocketClient();
    }

    public static OrderbookEndpoint derivateEndpoint(Map<Pair, Set<Consumer<Orderbook>>> derivateListeners) {
        return new OrderbookEndpoint(derivateListeners, BybitApiConfig.V5_PUBLIC_LINEAR);
    }

    public static OrderbookEndpoint spotEndpoint(Map<Pair, Set<Consumer<Orderbook>>> spotListeners) {
        return new OrderbookEndpoint(spotListeners, BybitApiConfig.V5_PUBLIC_SPOT);
    }

    private void onError() {
        setWebsocketClient();
        startListen();
    }

    private void setWebsocketClient() {
        websocketClient = new WebSocketClientDelegate(endpoint, false, new SocketMessageHandler(marketDataConsumer), this::onError);
    }

    public void startListen() {
        List<String> bybitPairs = pairs.stream().map(this::convertPairs).distinct().toList();
        websocketClient.getPublicChannelStream(bybitPairs, urlPath);
    }

    public void stopListen() {
        websocketClient.disconnect();
    }

    private String convertPairs(Pair pair) {
        PairConverter pairConverter = new PairConverter();
        return "orderbook.200." + pairConverter.convert(pair);
    }

    private static class SocketMessageHandler implements WebsocketMessageHandler {

        private final Map<Pair, Set<Consumer<Orderbook>>> orderBookConsumers;
        private final PairConverter pairConverter;
        private Map<Pair, Orderbook> orderbook;

        private SocketMessageHandler(Map<Pair, Set<Consumer<Orderbook>>> orderBookConsumers) {
            this.orderBookConsumers = orderBookConsumers;
            this.pairConverter = new PairConverter();
            orderbook = new HashMap<>();
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
            Orderbook create = new Orderbook(convert.get(), map(orderBookResponse.getTs()), orderBookResponse.getType(),
                    bidValues, bidQuantities, asksValues, asksQuantities);
            orderBookConsumers.getOrDefault(convert.get(), Set.of()).forEach(consumer -> consumer.accept(create));
        }

//        private void consume(MarketData marketData) {
//            Set<Consumer<MarketData>> marketDataConsumer = marketDataConsumers.get(marketData.pair());
//            if (marketDataConsumer == null) {
//                return;
//            }
//
//            marketDataConsumer.forEach(consumer -> consumer.accept(marketData));
//        }
//
//        private Optional<MarketData> map(String epic, MarketDataResponseData marketDataResponseData) {
//            Optional<Pair> convert = pairConverter.convert(epic, marketDataResponseData.getInterval());
//            if (convert.isEmpty()) {
//                return Optional.empty();
//            }
//
//            Decimal open = new Decimal(marketDataResponseData.getOpen());
//            Decimal high = new Decimal(marketDataResponseData.getHigh());
//            Decimal low = new Decimal(marketDataResponseData.getLow());
//            Decimal close = new Decimal(marketDataResponseData.getClose());
//            Decimal volume = new Decimal(marketDataResponseData.getVolume());
//
//            return Optional.of(new MarketData(convert.get(), map(marketDataResponseData.getStart()),
//                    open, open,
//                    high, high,
//                    low, low,
//                    close, close,
//                    volume));
//        }

        private LocalDateTime map(long start) {
            return Instant.ofEpochMilli(start)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
    }
}
