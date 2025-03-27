package com.becker.freelance.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Trade;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

public class CapitalTradeExecutor extends TradeExecutor {

    public CapitalTradeExecutor() {
    }

    public CapitalTradeExecutor(Pair pair) {

    }

    @Override
    protected TradeExecutor construct(Pair pair) {
        return new CapitalTradeExecutor(pair);
    }

    @Override
    protected TradeExecutor construct(BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    protected boolean supports(AppMode appMode) {
        return appMode.isDemo() && appMode.getDataSourceName().equals("CAPITAL");
    }

    @Override
    public void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice) {

    }

    @Override
    public void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal) {

    }

    @Override
    public void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal) {

    }

    @Override
    public List<Trade> getAllClosedTrades() {
        return List.of();
    }

    @Override
    public BacktestWallet getWallet() {
        return null;
    }

    @Override
    protected void setWallet(Supplier<BacktestWallet> wallet) {

    }

    @Override
    public void adaptPositions(TimeSeriesEntry currentPrice) {

    }

    @Override
    protected Pair getPair() {
        return null;
    }

    @Override
    public boolean isPositionOpen(Pair pair) {
        return false;
    }
}
