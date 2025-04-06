package com.becker.freelance.broker.orderbook;

import com.becker.freelance.commons.pair.Pair;

import java.util.function.Consumer;

public interface OrderBookListener extends Consumer<Orderbook> {

    public void onOrderbook(Orderbook orderbook);

    public Pair supportedPair();

    @Override
    default void accept(Orderbook orderbook) {
        onOrderbook(orderbook);
    }
}
