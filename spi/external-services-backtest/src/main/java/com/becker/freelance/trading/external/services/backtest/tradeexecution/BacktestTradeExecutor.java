package com.becker.freelance.trading.external.services.backtest.tradeexecution;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.commons.wallet.Wallet;
import com.becker.freelance.trading.external.services.tradeexecution.TradeExecutor;

import java.util.List;
import java.util.function.Supplier;

public interface BacktestTradeExecutor extends TradeExecutor {

    public Wallet getWallet();

    void setWallet(Supplier<Wallet> wallet);

    Pair getPair();

    List<Trade> getAllClosedTrades();

}
