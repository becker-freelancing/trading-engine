package com.becker.freelance.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.calculation.PositionCalculation;
import com.becker.freelance.commons.calculation.PositionCalculation.PositionCalculationResult;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.Trade;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DemoTradeExecutor extends TradeExecutor {

    private Supplier<BacktestWallet> wallet;
    private List<Position> openPositions;
    private List<Trade> closedTrades;
    private PositionCalculation positionCalculation;
    private TradingCalculator tradingCalculator;
    private Pair pair;

    public DemoTradeExecutor(){

    }

    private DemoTradeExecutor(BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair) {
        BacktestWallet wallet = new BacktestWallet(backtestExecutionConfiguration.initialWalletAmount());
        this.wallet = () -> wallet;
        openPositions = new ArrayList<>();
        closedTrades = new ArrayList<>();
        this.pair = pair;
        tradingCalculator = new TradingCalculator(pair, backtestExecutionConfiguration.getEurUsdTimeSeries());
        MarginCalculator marginCalculator = new MarginCalculator(pair, backtestExecutionConfiguration.getEurUsdTimeSeries());
        positionCalculation = new PositionCalculation(tradingCalculator, marginCalculator);
    }

    @Override
    protected TradeExecutor construct(BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair) {
        return new DemoTradeExecutor(backtestExecutionConfiguration, pair);
    }

    @Override
    protected TradeExecutor construct(Pair pair) {
        throw new UnsupportedOperationException("DemoTrade Executor needs BacktestConfiguration for construction");
    }

    @Override
    protected boolean supports(AppMode appMode) {
        return appMode.isDemo();
    }

    @Override
    public void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice) {
        PositionCalculationResult closePositionResults = positionCalculation.closePositionIfSlOrTpReached(currentPrice, openPositions, wallet.get());
        openPositions = closePositionResults.positions();
        closedTrades.addAll(closePositionResults.trades());
    }

    @Override
    public void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal) {
        PositionCalculationResult closePositionResults;
        if (exitSignal.getDirectionToClose() == Direction.BUY){
            closePositionResults = positionCalculation.closeAllBuyPositions(currentPrice, openPositions, wallet.get());
        } else {
            closePositionResults = positionCalculation.closeAllSellPositions(currentPrice, openPositions, wallet.get());
        }
        List<Trade> trades = closePositionResults.trades();
        openPositions = closePositionResults.positions();
        closedTrades.addAll(trades);
    }

    @Override
    public void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal) {
        PositionCalculationResult openPositionsResult = positionCalculation.openPosition(currentPrice, openPositions, entrySignal, wallet.get());
        openPositions = openPositionsResult.positions();
        closedTrades.addAll(openPositionsResult.trades());
    }

    @Override
    public List<Trade> getAllClosedTrades() {
        return closedTrades;
    }

    @Override
    public BacktestWallet getWallet() {
        return wallet.get();
    }

    @Override
    public void adaptPositions(TimeSeriesEntry currentPrice) {
        for (Position openPosition : openPositions) {
            openPosition.adapt(currentPrice);
        }
    }

    @Override
    public boolean isPositionOpen(Pair pair) {
        return openPositions.stream().anyMatch(pos -> pair.equals(pos.getPair()));
    }

    @Override
    protected void setWallet(Supplier<BacktestWallet> wallet) {
        this.wallet = wallet;
    }

    @Override
    protected Pair getPair() {
        return pair;
    }
}
