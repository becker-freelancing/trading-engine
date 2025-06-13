package com.becker.freelance.bybit.trades;

import com.becker.freelance.commons.order.ConditionalOrder;
import com.becker.freelance.commons.order.LimitOrder;
import com.becker.freelance.commons.order.MarketOrder;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.trade.Trade;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TradeController {

    private final Logger logger = LoggerFactory.getLogger(TradeController.class);

    private final TradeApiClient apiClient;

    public TradeController() {
        apiClient = new TradeApiClient();
    }

    public List<Position> allPositions() {
        List<PositionResponse> allPositionsResponse;
        try {
            allPositionsResponse = apiClient.allPositions();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            logger.error("Could not request all open Positions", e);
            return List.of();
        }

        return allPositionsResponse.stream().map(this::toPosition).toList();
    }

    private Position toPosition(PositionResponse positionResponse) {
        return new BybitPosition(positionResponse);
    }


    public List<Trade> getTradesForDurationUntilNowForPair(LocalDateTime to, Duration duration, Pair pair) {
        LocalDateTime from = to.minus(duration);
        return apiClient.getTradesInTime(from, to, pair)
                .toList();
    }

    public Optional<OrderPlacementResult> createConditionalOrder(ConditionalOrder conditionalOrder) {
        return apiClient.createConditionalOrder(conditionalOrder);
    }

    public Optional<OrderPlacementResult> createLimitOrder(LimitOrder limitOrder) {
        return apiClient.createLimitOrder(limitOrder);
    }

    public Optional<OrderPlacementResult> createMarketOrder(MarketOrder marketOrder) {
        return apiClient.marketOrder(marketOrder);

    }

    public Optional<OrderCancelFailReason> cancelOrder(String orderId, Pair pair) {
        return apiClient.cancelOrder(orderId, pair);
    }

    public void entry(EntrySignal entrySignal) {
        ByBitEntrySignalConverter byBitEntrySignalConverter = new ByBitEntrySignalConverter();
        TradeOrderRequest convert = byBitEntrySignalConverter.convert(entrySignal);
        apiClient.execute(convert);
    }
}
