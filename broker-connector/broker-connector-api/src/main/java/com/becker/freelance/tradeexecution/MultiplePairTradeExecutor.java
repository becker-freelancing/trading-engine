package com.becker.freelance.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.trade.Trade;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

class MultiplePairTradeExecutor extends TradeExecutor {

    private final Map<Pair, TradeExecutor> tradeExecutors;
    private BacktestWallet wallet;

    public MultiplePairTradeExecutor(List<TradeExecutor> tradeExecutorsForPairs, BacktestExecutionConfiguration backtestExecutionConfiguration) {
        this.wallet = new BacktestWallet(backtestExecutionConfiguration.initialWalletAmount());
        tradeExecutors = new HashMap<>();
        tradeExecutorsForPairs.stream()
                .peek(tradeExecutor -> tradeExecutor.setWallet(getWalletSupplier()))
                .forEach(executor -> tradeExecutors.put(executor.getPair(), executor));
    }

    @Override
    protected TradeExecutor construct(BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair, EurUsdRequestor eurUsdRequestor) {
        throw new UnsupportedOperationException("MultiplePairTradeExecutor must be constructed explicitly");
    }

    @Override
    protected TradeExecutor construct(Pair pair, EurUsdRequestor eurUsdRequestor) {
        throw new UnsupportedOperationException("MultiplePairTradeExecutor must be constructed explicitly");
    }

    @Override
    protected boolean supports(AppMode appMode) {
        return tradeExecutors.values().stream()
                .map(executor -> executor.supports(appMode))
                .reduce((a, b) -> a && b)
                .orElse(false);
    }

    @Override
    public void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice) {
        Pair pair = currentPrice.pair();
        tradeExecutors.get(pair).closePositionsIfSlOrTpReached(currentPrice);
    }

    @Override
    public void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal) {
        Pair pair = currentPrice.pair();
        tradeExecutors.get(pair).exit(currentPrice, timeSeries, time, exitSignal);
    }

    @Override
    public void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal) {
        Pair pair = currentPrice.pair();
        tradeExecutors.get(pair).entry(currentPrice, timeSeries, time, entrySignal);
    }

    @Override
    public List<Trade> getAllClosedTrades() {
        return tradeExecutors.values().stream()
                .map(TradeExecutor::getAllClosedTrades)
                .reduce(((trades, trades2) -> {
                    List<Trade> combined = new ArrayList<>();
                    combined.addAll(trades);
                    combined.addAll(trades2);
                    return combined;
                })).orElse(List.of()).stream()
                .sorted(Comparator.comparing(Trade::getOpenTime))
                .toList();

    }

    @Override
    protected void setWallet(Supplier<BacktestWallet> wallet) {
        this.wallet = wallet.get();
    }

    private Supplier<BacktestWallet> getWalletSupplier() {
        return () -> wallet;
    }

    @Override
    public void adaptPositions(TimeSeriesEntry currentPrice) {
        Pair pair = currentPrice.pair();
        tradeExecutors.get(pair).adaptPositions(currentPrice);
    }

    @Override
    protected Pair getPair() {
        throw new UnsupportedOperationException("MultiplePairTradeExecutor supports multiple pairs");
    }

    @Override
    public boolean isPositionOpen(Pair pair) {
        return tradeExecutors.get(pair).isPositionOpen(pair);
    }
}
