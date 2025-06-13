package com.becker.freelance.bybit.trades;

import com.becker.freelance.bybit.util.PairConverter;
import com.becker.freelance.commons.order.ConditionalOrder;
import com.becker.freelance.commons.order.LimitOrder;
import com.becker.freelance.commons.order.MarketOrder;
import com.becker.freelance.commons.order.Order;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.signal.EntrySignal;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;

import java.util.UUID;

public class ByBitEntrySignalConverter {

    private final PairConverter pairConverter;

    public ByBitEntrySignalConverter() {
        pairConverter = new PairConverter();
    }

    public TradeOrderRequest convert(EntrySignal entrySignal) {
        TradeOrderRequest.TradeOrderRequestBuilder builder = TradeOrderRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol(pairConverter.convert(entrySignal.pair()))
                .isLeverage(1)
                .side(map(entrySignal.getOpenOrder().getDirection()))
                .orderType(map(entrySignal.getOpenOrder()))
                .qty(entrySignal.getOpenOrder().getSize().toPlainString())
                .marketUnit(entrySignal.pair().baseCurrency())
                .takeProfit(((LimitOrder) entrySignal.getLimitOrder()).getOrderPrice().toPlainString())
                .tpOrderType(TradeOrderType.LIMIT)
                .stopLoss(((ConditionalOrder) entrySignal.getStopOrder()).getThresholdPrice().toPlainString())
                .slOrderType(TradeOrderType.MARKET)
                .orderLinkId(UUID.randomUUID().toString());

        if (entrySignal.getOpenOrder() instanceof LimitOrder lazyOrder) {
            builder = builder.price(lazyOrder.getOrderPrice().toPlainString());
        }

        return builder.build();
    }

    private Side map(Direction direction) {
        return switch (direction) {
            case BUY -> Side.BUY;
            case SELL -> Side.SELL;
        };
    }

    private TradeOrderType map(Order order) {
        if (order instanceof MarketOrder) {
            return TradeOrderType.MARKET;
        } else if (order instanceof LimitOrder) {
            return TradeOrderType.LIMIT;
        } else if (order instanceof ConditionalOrder conditionalOrder) {
            return map(conditionalOrder.getDelegateOrder());
        }

        throw new IllegalStateException("Order Type not supported");
    }
}
