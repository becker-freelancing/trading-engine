package com.becker.freelance.bybit.trades;

import com.becker.freelance.bybit.env.EnvironmentProvider;
import com.becker.freelance.bybit.util.PairConverter;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.math.Decimal;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.position.request.PositionDataRequest;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.restApi.BybitApiPositionRestClient;
import com.bybit.api.client.restApi.BybitApiTradeRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

class TradeApiClient {

    private final static Logger logger = LoggerFactory.getLogger(TradeApiClient.class);

    private final BybitApiTradeRestClient tradeRestClient;
    private final BybitApiPositionRestClient positionRestClient;
    private final PairConverter pairConverter;

    public TradeApiClient() {
        EnvironmentProvider environmentProvider = new EnvironmentProvider();
        BybitApiClientFactory bybitApiClientFactory = BybitApiClientFactory.newInstance(environmentProvider.apiKey(), environmentProvider.secret(), environmentProvider.baseURL());
        this.tradeRestClient = bybitApiClientFactory.newTradeRestClient();
        this.positionRestClient = bybitApiClientFactory.newPositionRestClient();
        this.pairConverter = new PairConverter();
    }

    public List<PositionResponse> allPositions() throws URISyntaxException, IOException, InterruptedException {
        return Pair.allPairs().stream()
                .map(Pair::counterCurrency).distinct()
                .map(counterCurrency -> positionRestClient.getPositionInfo(PositionDataRequest.builder().limit(200).category(CategoryType.LINEAR).settleCoin(counterCurrency).build()))
                .map(response -> (Map<String, Object>) response)
                .flatMap(this::mapPositions)
                .toList();
    }

    private Stream<PositionResponse> mapPositions(Map<String, Object> response) {
        Map<String, Map<String, Object>> result = (Map<String, Map<String, Object>>) response.get("result");
        List<Map<String, Object>> positionList = (List<Map<String, Object>>) result.get("list");

        return positionList.stream()
                .map(this::mapPosition);
    }

    private PositionResponse mapPosition(Map<String, Object> position) {
        return new PositionResponse(
                pairConverter.convert((String) position.get("symbol"), "1").orElseThrow(() -> new IllegalArgumentException("Could not map Pair " + position.get("symbol"))), //TODO
                new Decimal((String) position.get("size")),
                "Buy".equals(position.get("side")) ? Direction.BUY : Direction.SELL,
                new Decimal((String) position.get("avgPrice")), //TODO
                toLocalDateTime((String) position.get("createdTime")),
                new Decimal((String) position.get("positionIM")),
                new Decimal((String) position.get("stopLoss")),
                new Decimal((String) position.get("takeProfit")),
                UUID.randomUUID().toString()
        );
    }

    private LocalDateTime toLocalDateTime(String createdTime) {
        return Instant.ofEpochMilli(Long.parseLong(createdTime))
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


    public Optional<String> createPositionStopLimitLevel(Direction direction, Pair pair, Decimal size, Decimal stopLevel, Decimal limitLevel) throws IOException, InterruptedException, URISyntaxException {
        TradeOrderRequest orderRequest = TradeOrderRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol(pairConverter.convert(pair))
                .isLeverage(1)
                .side(direction == Direction.BUY ? Side.BUY : Side.SELL)
                .orderType(TradeOrderType.MARKET)
                .qty(size.toPlainString())
                .marketUnit(pair.baseCurrency())
                .orderLinkId(UUID.randomUUID().toString())
                .takeProfit(limitLevel.toPlainString())
                .stopLoss(stopLevel.toPlainString())
                .tpOrderType(TradeOrderType.MARKET)
                .slOrderType(TradeOrderType.MARKET)
                .tpslMode("Full")
                .build();
        Map<String, Object> order = (Map<String, Object>) tradeRestClient.createOrder(orderRequest);
        if (!"OK".equals(order.get("retMsg"))) {
            logger.warn("Could not open order for pair {}, because: {}", pair.technicalName(), order.get("retMsg"));
            return Optional.empty();
        }
        logger.info("Order opened for {}, (Direction: {}, Stop: {}, Limit: {})", pair.technicalName(), direction, stopLevel, limitLevel);
        Map<String, String> result = (Map<String, String>) order.get("result");
        return Optional.ofNullable(result.get("id"));
    }


    public void marketOrder(Pair pair, Direction direction, Decimal size) {
        TradeOrderRequest orderRequest = TradeOrderRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol(pairConverter.convert(pair))
                .isLeverage(1)
                .side(direction == Direction.BUY ? Side.BUY : Side.SELL)
                .orderType(TradeOrderType.MARKET)
                .qty(size.toPlainString())
                .marketUnit(pair.baseCurrency())
                .orderLinkId(UUID.randomUUID().toString())
                .reduceOnly(true)
                .build();

        tradeRestClient.createOrder(orderRequest);
    }

}