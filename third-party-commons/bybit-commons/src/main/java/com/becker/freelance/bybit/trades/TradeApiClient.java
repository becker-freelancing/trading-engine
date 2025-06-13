package com.becker.freelance.bybit.trades;

import com.becker.freelance.bybit.env.BybitEnvironmentProvider;
import com.becker.freelance.bybit.util.PairConverter;
import com.becker.freelance.commons.order.ConditionalOrder;
import com.becker.freelance.commons.order.LimitOrder;
import com.becker.freelance.commons.order.MarketOrder;
import com.becker.freelance.commons.order.TriggerDirection;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.trade.Trade;
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
import java.time.ZoneOffset;
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
        BybitEnvironmentProvider bybitEnvironmentProvider = new BybitEnvironmentProvider();
        BybitApiClientFactory bybitApiClientFactory = BybitApiClientFactory.newInstance(bybitEnvironmentProvider.apiKey(), bybitEnvironmentProvider.secret(), bybitEnvironmentProvider.baseURL());
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

    public Stream<Trade> getTradesInTime(LocalDateTime from, LocalDateTime to, Pair pair) {
        Stream<Trade> result = Stream.of();
        while (from.isBefore(to)) {
            long startMillis = from.toInstant(ZoneOffset.UTC).toEpochMilli();
            LocalDateTime currEnd = from.plusHours(1);
            long endMillis = currEnd.toInstant(ZoneOffset.UTC).toEpochMilli();

            result = requestTradesInTime(startMillis, endMillis, result, null);
            from = currEnd;
        }

        return result.filter(trade -> trade.getPair().equalsIgnoreResolution(pair));
    }

    private Stream<Trade> requestTradesInTime(long startMillis, long endMillis, Stream<Trade> result, String cursor) {
        PositionDataRequest.PositionDataRequestBuilder positionDataRequestBuilder = PositionDataRequest.builder().category(CategoryType.LINEAR);

        if (cursor == null) {
            positionDataRequestBuilder = positionDataRequestBuilder
                    .startTime(startMillis)
                    .endTime(endMillis);
        } else {
            positionDataRequestBuilder = positionDataRequestBuilder.cursor(cursor);
        }

        Map<String, Object> closePnlList = (Map<String, Object>) positionRestClient.getClosePnlList(positionDataRequestBuilder
                .build());

        if (0 != (int) closePnlList.get("retCode")) {
            logger.error("Could not request trade history. Error-Code: {}, Error-Message: {}", closePnlList.get("retCode"), closePnlList.get("retMsg"));
        }

        Map<String, Object> responseResult = (Map<String, Object>) closePnlList.get("result");
        List<Map<String, String>> list = (List<Map<String, String>>) responseResult.get("list");


        result = Stream.concat(result, list.stream()
                .map(this::toTrade));
        return result;
    }

    private Trade toTrade(Map<String, String> stringStringMap) {
        return new Trade(
                stringStringMap.get("orderId"),
                Instant.ofEpochMilli(Long.valueOf(stringStringMap.get("createdTime"))).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                Instant.ofEpochMilli(Long.valueOf(stringStringMap.get("updatedTime"))).atZone(ZoneId.systemDefault()).toLocalDateTime(), //TODO
                pairConverter.convert(stringStringMap.get("symbol"), "1").orElseThrow(() -> new IllegalArgumentException("Could not convert pair: " + stringStringMap.get("symbol"))),
                new Decimal(stringStringMap.get("closedPnl")),
                new Decimal(stringStringMap.get("avgEntryPrice")),
                new Decimal(stringStringMap.get("avgExitPrice")),
                Decimal.ZERO,
                Decimal.ZERO,
                new Decimal(stringStringMap.get("qty")),
                stringStringMap.get("side").equalsIgnoreCase("BUY") ? Direction.BUY : Direction.SELL,
                Decimal.ONE,
                PositionBehaviour.HARD_LIMIT,
                null
        );
    }

    public Optional<OrderPlacementResult> createConditionalOrder(ConditionalOrder conditionalOrder) {
        TradeOrderRequest.TradeOrderRequestBuilder orderRequest = TradeOrderRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol(pairConverter.convert(conditionalOrder.getPair()))
                .isLeverage(1)
                .side(map(conditionalOrder.getDirection()))
                .qty(conditionalOrder.getSize().toPlainString())
                .marketUnit(conditionalOrder.getPair().baseCurrency())
                .reduceOnly(conditionalOrder.isReduceOnly())
                .triggerPrice(conditionalOrder.getThresholdPrice().toPlainString())
                .triggerDirection(map(conditionalOrder.getTriggerDirection()));

        if (conditionalOrder.getDelegateOrder() instanceof MarketOrder) {
            orderRequest = orderRequest
                    .orderType(TradeOrderType.MARKET);
        } else if (conditionalOrder.getDelegateOrder() instanceof LimitOrder limitOrder) {
            orderRequest = orderRequest
                    .orderType(TradeOrderType.LIMIT)
                    .price(limitOrder.getOrderPrice().toPlainString());
        } else {
            throw new UnsupportedOperationException("Delegate of Type " + conditionalOrder.getDelegateOrder().getClass() + " is not supported");
        }

        Map<String, Object> order = (Map<String, Object>) tradeRestClient.createOrder(orderRequest.build());

        if (!"OK".equals(order.get("retMsg"))) {
            logger.warn("Could not place conditional order for pair {}, because: {}, Order: {}", conditionalOrder.getPair().technicalName(), order.get("retMsg"), conditionalOrder);
            return Optional.empty();
        }
        logger.info("Conditional Order placed on {}, ({})", conditionalOrder.getPair().technicalName(), conditionalOrder);
        Map<String, String> result = (Map<String, String>) order.get("result");

        return Optional.of(new OrderPlacementResult(result.get("id")));
    }

    private Integer map(TriggerDirection triggerDirection) {
        return switch (triggerDirection) {
            case DOWN_CROSS -> 2;
            case UP_CROSS -> 1;
        };
    }

    public Optional<OrderPlacementResult> createLimitOrder(LimitOrder limitOrder) {
        TradeOrderRequest orderRequest = TradeOrderRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol(pairConverter.convert(limitOrder.getPair()))
                .isLeverage(1)
                .side(map(limitOrder.getDirection()))
                .orderType(TradeOrderType.LIMIT)
                .qty(limitOrder.getSize().toPlainString())
                .marketUnit(limitOrder.getPair().baseCurrency())
                .reduceOnly(limitOrder.isReduceOnly())
                .price(limitOrder.getOrderPrice().toPlainString())
                .build();

        Map<String, Object> order = (Map<String, Object>) tradeRestClient.createOrder(orderRequest);

        if (!"OK".equals(order.get("retMsg"))) {
            logger.warn("Could not place Limit order for pair {}, because: {}, Order: {}", limitOrder.getPair().technicalName(), order.get("retMsg"), limitOrder);
            return Optional.empty();
        }
        logger.info("Limit Order placed on {}, ({})", limitOrder.getPair().technicalName(), limitOrder);
        Map<String, String> result = (Map<String, String>) order.get("result");

        return Optional.of(new OrderPlacementResult(result.get("id")));
    }


    public Optional<OrderPlacementResult> marketOrder(MarketOrder marketOrder) {
        TradeOrderRequest orderRequest = TradeOrderRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol(pairConverter.convert(marketOrder.getPair()))
                .isLeverage(1)
                .side(map(marketOrder.getDirection()))
                .orderType(TradeOrderType.MARKET)
                .qty(marketOrder.getSize().toPlainString())
                .marketUnit(marketOrder.getPair().baseCurrency())
                .reduceOnly(marketOrder.isReduceOnly())
                .build();

        Map<String, Object> order = (Map<String, Object>) tradeRestClient.createOrder(orderRequest);

        if (!"OK".equals(order.get("retMsg"))) {
            logger.warn("Could not open market order for pair {}, because: {}", marketOrder.getPair().technicalName(), order.get("retMsg"));
            return Optional.empty();
        }
        logger.info("Market Order placed on {}, (Direction: {})", marketOrder.getPair().technicalName(), marketOrder.getDirection());
        Map<String, String> result = (Map<String, String>) order.get("result");

        return Optional.of(new OrderPlacementResult(result.get("id")));
    }

    private Side map(Direction direction) {
        return switch (direction) {
            case BUY -> Side.BUY;
            case SELL -> Side.SELL;
        };
    }

    public Optional<OrderCancelFailReason> cancelOrder(String orderId, Pair pair) {
        TradeOrderRequest orderRequest = TradeOrderRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol(pairConverter.convert(pair))
                .orderId(orderId)
                .build();

        Map<String, Object> result = (Map<String, Object>) tradeRestClient.cancelOrder(orderRequest);
        if (!"OK".equals(result.get("retMsg"))) {
            logger.warn("Could not cancel order for pair {} with id {}, because: {}", pair, orderId, result.get("retMsg"));
            return Optional.of(OrderCancelFailReason.UNKNOWN);
        }

        return Optional.empty();
    }

    public void execute(TradeOrderRequest convert) {
        Map<String, Object> result = (Map<String, Object>) tradeRestClient.cancelOrder(convert);
        if (!"OK".equals(result.get("retMsg"))) {
            logger.warn("Could not open Order, because: {}", result.get("retMsg"));
            return;
        }

        logger.info("Order opened {}", convert);
    }
}