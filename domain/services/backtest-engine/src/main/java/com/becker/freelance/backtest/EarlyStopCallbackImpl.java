package com.becker.freelance.backtest;

import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.trading.external.services.backtest.earlystop.*;
import com.becker.freelance.trading.external.services.registry.ExternalServiceRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class EarlyStopCallbackImpl implements Consumer<Trade> {

    private final EarlyStopCallback delegate;
    private final List<Trade> trades;
    private int lastExecutedTradesSize;

    public EarlyStopCallbackImpl(Decimal initialWalletAmount) {
        this.delegate = ExternalServiceRegistry.globalServiceRegistry().requireServiceBuilder(EarlyStopCallbackBuilder.class).build(
                new EarlyStopBuilderParams(
                        initialWalletAmount
                )
        );
        this.trades = new ArrayList<>();
        this.lastExecutedTradesSize = 0;
    }

    public EarlyStopCallbackResult shouldStop() {
        if (lastExecutedTradesSize >= trades.size()) {
            return new NoStopEarlyStopCallbackResult();
        }

        lastExecutedTradesSize = trades.size();
        return delegate.shouldStop(() -> trades);
    }

    @Override
    public void accept(Trade trade) {
        trades.add(trade);
    }
}
