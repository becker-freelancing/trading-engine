package com.becker.freelance.bybit.marketdata;

import com.becker.freelance.broker.marketdata.MarketData;
import com.becker.freelance.bybit.env.EnvironmentProvider;
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
import java.util.function.Consumer;

class MarketDataEndpoint {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(MarketDataEndpoint.class);


    private final String urlPath;

    private MarketDataEndpoint(Map<Pair, Set<Consumer<MarketData>>> marketDataConsumer, String urlPath) {
        this.pairs = marketDataConsumer.keySet();
        this.marketDataConsumer = marketDataConsumer;
        this.urlPath = urlPath;
        String endpoint = BybitApiConfig.STREAM_MAINNET_DOMAIN;
        if (new EnvironmentProvider().isDemo()) {
            endpoint = BybitApiConfig.STREAM_TESTNET_DOMAIN;
        }
        this.endpoint = endpoint;
        setWebsocketClient();
    }

    private final Set<Pair> pairs;
    private final Map<Pair, Set<Consumer<MarketData>>> marketDataConsumer;

    public static MarketDataEndpoint derivateEndpoint(Map<Pair, Set<Consumer<MarketData>>> derivateListeners) {
        return new MarketDataEndpoint(derivateListeners, BybitApiConfig.V5_PUBLIC_LINEAR);
    }
    private final String endpoint;
    private WebSocketClientDelegate websocketClient;

    public static MarketDataEndpoint spotEndpoint(Map<Pair, Set<Consumer<MarketData>>> spotListeners) {
        return new MarketDataEndpoint(spotListeners, BybitApiConfig.V5_PUBLIC_SPOT);
    }

    private void onError() {
        setWebsocketClient();
        startListen();
    }

    private void setWebsocketClient() {
        websocketClient = new WebSocketClientDelegate(endpoint, false, new SocketMessageHandler(marketDataConsumer), this::onError);
    }

    public void startListen() {
        List<String> bybitPairs = pairs.stream().map(this::convertPairs).toList();
        websocketClient.getPublicChannelStream(bybitPairs, urlPath);
    }

    public void stopListen() {
        websocketClient.disconnect();
    }

    private String convertPairs(Pair pair) {
        PairConverter pairConverter = new PairConverter();
        return "kline." + pairConverter.convertResolution(pair) + "." + pairConverter.convert(pair);
    }

    private static class SocketMessageHandler implements WebsocketMessageHandler {

        private final Map<Pair, Set<Consumer<MarketData>>> marketDataConsumers;
        private final PairConverter pairConverter;

        private SocketMessageHandler(Map<Pair, Set<Consumer<MarketData>>> marketDataConsumers) {
            this.marketDataConsumers = marketDataConsumers;
            this.pairConverter = new PairConverter();
        }


        @Override
        public void handleMessage(String message) {
            if (!message.contains("topic")) {
                return;
            }
            try {
                MarketDataResponse marketDataResponse = objectMapper.readValue(message, MarketDataResponse.class);
                String epic = marketDataResponse.getTopic().split("\\.")[2];
                marketDataResponse.getData().stream()
                        .filter(MarketDataResponseData::getConfirm)
                        .map(marketDataResponseData -> map(epic, marketDataResponseData))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(this::consume);
            } catch (JsonProcessingException e) {
                logger.error("Could not convert to MarketData: {}", message, e);
            }
        }

        private void consume(MarketData marketData) {
            Set<Consumer<MarketData>> marketDataConsumer = marketDataConsumers.get(marketData.pair());
            if (marketDataConsumer == null) {
                return;
            }

            marketDataConsumer.forEach(consumer -> consumer.accept(marketData));
        }

        private Optional<MarketData> map(String epic, MarketDataResponseData marketDataResponseData) {
            Optional<Pair> convert = pairConverter.convert(epic, marketDataResponseData.getInterval());
            if (convert.isEmpty()) {
                return Optional.empty();
            }

            Decimal open = new Decimal(marketDataResponseData.getOpen());
            Decimal high = new Decimal(marketDataResponseData.getHigh());
            Decimal low = new Decimal(marketDataResponseData.getLow());
            Decimal close = new Decimal(marketDataResponseData.getClose());
            Decimal volume = new Decimal(marketDataResponseData.getVolume());

            return Optional.of(new MarketData(convert.get(), map(marketDataResponseData.getStart()),
                    open, open,
                    high, high,
                    low, low,
                    close, close,
                    volume));
        }

        private LocalDateTime map(long start) {
            return Instant.ofEpochMilli(start)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
    }
}
