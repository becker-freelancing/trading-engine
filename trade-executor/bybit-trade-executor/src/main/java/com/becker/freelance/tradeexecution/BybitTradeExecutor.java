package com.becker.freelance.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.bybit.trades.TradeController;
import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.signal.LevelEntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.tradeexecution.util.calculation.TradingCalculatorImpl;
import com.becker.freelance.tradeexecution.util.signal.EntrySignalConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

public class BybitTradeExecutor extends TradeExecutor {

    private TradeController tradeController;
    private TradingCalculator tradingCalculator;

    public BybitTradeExecutor() {
    }

    public BybitTradeExecutor(Pair pair, EurUsdRequestor eurUsdRequestor) {
        tradeController = new TradeController();
        tradingCalculator = new TradingCalculatorImpl(eurUsdRequestor);
    }

    @Override
    protected TradeExecutor construct(Pair pair, EurUsdRequestor eurUsdRequestor) {
        return new BybitTradeExecutor(pair, eurUsdRequestor);
    }

    @Override
    protected TradeExecutor construct(BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair, EurUsdRequestor eurUsdRequestor) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    protected boolean supports(AppMode appMode) {
        return appMode.isDemo() && appMode.getDataSourceName().equals("BYBIT");
    }

    @Override
    public void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice) {
//Not needed -> Only for local backtest
    }

    @Override
    public void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal) {
        tradeController.closePositions(currentPrice.pair(), exitSignal);
    }

    @Override
    public void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal) {

        EntrySignalConverter entrySignalConverter = new EntrySignalConverter(tradingCalculator);
        entrySignal.visit(entrySignalConverter);
        LevelEntrySignal levelEntrySignal = entrySignalConverter.getConvertion();

        tradeController.createPositionStopLimitLevel(
                levelEntrySignal.getDirection(),
                currentPrice.pair(),
                levelEntrySignal.getSize(),
                levelEntrySignal.stopLevel(),
                levelEntrySignal.limitLevel()
        );
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
