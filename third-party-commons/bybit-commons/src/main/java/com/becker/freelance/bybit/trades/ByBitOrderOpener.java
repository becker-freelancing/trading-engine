package com.becker.freelance.bybit.trades;

import com.becker.freelance.commons.order.ConditionalOrder;
import com.becker.freelance.commons.order.LimitOrder;
import com.becker.freelance.commons.order.MarketOrder;
import com.becker.freelance.commons.order.OrderVisitor;
import com.becker.freelance.commons.pair.Pair;

import java.util.*;
import java.util.stream.Collectors;

public final class ByBitOrderOpener implements OrderVisitor {

    private final TradeController tradeController;
    private final List<OrderPlacementResult> executedOrders;

    public ByBitOrderOpener(TradeController tradeController) {
        this.tradeController = tradeController;
        this.executedOrders = new ArrayList<>();
    }

    @Override
    public void accept(ConditionalOrder conditionalOrder) {
        tradeController.createConditionalOrder(conditionalOrder)
                .ifPresentOrElse(executedOrders::add, () -> cancelOpenedOrders(conditionalOrder.getPair()));
    }

    @Override
    public void accept(LimitOrder limitOrder) {

        tradeController.createLimitOrder(limitOrder)
                .ifPresentOrElse(executedOrders::add, () -> cancelOpenedOrders(limitOrder.getPair()));
    }

    @Override
    public void accept(MarketOrder marketOrder) {


        tradeController.createMarketOrder(marketOrder)
                .ifPresentOrElse(executedOrders::add, () -> cancelOpenedOrders(marketOrder.getPair()));
    }

    private void cancelOpenedOrders(Pair pair) {
        Set<String> orderIds = executedOrders.stream().map(OrderPlacementResult::orderId).collect(Collectors.toSet());

        cancelOpenedOrders(orderIds, pair);
    }

    private void cancelOpenedOrders(Set<String> orderIds, Pair pair) {
        if (orderIds.isEmpty()) {
            return;
        }
        Set<String> ordersToCancel = new HashSet<>();
        for (String orderId : orderIds) {
            Optional<OrderCancelFailReason> failReason = tradeController.cancelOrder(orderId, pair);
            failReason.filter(this::mustRetryOrderCancel).ifPresent(reason -> ordersToCancel.add(orderId));
        }

        cancelOpenedOrders(ordersToCancel, pair);
    }

    private boolean mustRetryOrderCancel(OrderCancelFailReason orderCancelFailReason) {
        return switch (orderCancelFailReason) {
            case UNKNOWN -> true;
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ByBitOrderOpener) obj;
        return Objects.equals(this.tradeController, that.tradeController);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeController);
    }

    @Override
    public String toString() {
        return "ByBitOrderOpener[" +
                "tradeController=" + tradeController + ']';
    }

    public void execute() {
    }
}
