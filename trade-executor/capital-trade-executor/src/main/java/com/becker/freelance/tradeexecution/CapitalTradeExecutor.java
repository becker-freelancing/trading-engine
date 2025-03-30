package com.becker.freelance.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.capital.trades.TradeController;
import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.*;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.trade.Trade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

public class CapitalTradeExecutor extends TradeExecutor {

    public CapitalTradeExecutor() {
    }

    private TradeController tradeController;

    public CapitalTradeExecutor(Pair pair) {
        tradeController = new TradeController();
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
//Not needed -> Only for local backtest
    }

    @Override
    public void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal) {
        tradeController.closePositions(exitSignal);
    }

    @Override
    public void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal) {

        if (entrySignal instanceof LevelEntrySignal levelEntrySignal) {
            tradeController.createPositionStopLimitLevel(
                    entrySignal.getDirection(),
                    currentPrice.pair(),
                    entrySignal.getSize(),
                    levelEntrySignal.getStopLevel(),
                    levelEntrySignal.getLimitLevel()
            );
        } else if (entrySignal instanceof DistanceEntrySignal distanceEntrySignal) {
            tradeController.createPositionStopLimitDistance(
                    entrySignal.getDirection(),
                    currentPrice.pair(),
                    entrySignal.getSize(),
                    distanceEntrySignal.getStopDistance(),
                    distanceEntrySignal.getLimitDistance()
            );
        } else if (entrySignal instanceof AmountEntrySignal amountEntrySignal) {
            tradeController.createPositionStopLimitAmount(
                    entrySignal.getDirection(),
                    currentPrice.pair(),
                    entrySignal.getSize(),
                    amountEntrySignal.getStopAmount(),
                    amountEntrySignal.getLimitAmount()
            );
        } else {
            throw new UnsupportedOperationException("Could not open position by EntrySignal " + entrySignal.getClass());
        }
    }

    @Override
    public List<Trade> getAllClosedTrades() {
        return List.of(); //Not needed -> Only for local backtest
    }

    @Override
    protected void setWallet(Supplier<BacktestWallet> wallet) {
//Not needed -> Only for local backtest
    }

    @Override
    public void adaptPositions(TimeSeriesEntry currentPrice) {
//Not needed -> Only for local backtest
    }

    @Override
    protected Pair getPair() {
        return null;//Not needed -> Only for local backtest
    }

    @Override
    public boolean isPositionOpen(Pair pair) {
        return tradeController.allPositions().stream()
                .anyMatch(position -> position.getPair().equals(pair));
    }
}
