package com.becker.freelance.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.calculation.TradingCalculatorImpl;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionFactory;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.signal.LevelEntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.tradeexecution.calculation.MarginCalculatorImpl;
import com.becker.freelance.tradeexecution.calculation.PositionAdaptor;
import com.becker.freelance.tradeexecution.calculation.PositionCalculation;
import com.becker.freelance.tradeexecution.calculation.PositionCalculation.PositionCalculationResult;
import com.becker.freelance.tradeexecution.position.DemoPositionFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class DemoTradeExecutor extends TradeExecutor {

    private Supplier<BacktestWallet> wallet;
    private List<Position> openPositions;
    private List<Trade> closedTrades;
    private PositionCalculation positionCalculation;
    private Pair pair;
    private PositionFactory positionFactory;
    private PositionAdaptor positionAdaptor;
    private TradingCalculator tradingCalculator;

    public DemoTradeExecutor(){

    }

    private DemoTradeExecutor(BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair, EurUsdRequestor eurUsdRequestor) {
        BacktestWallet wallet = new BacktestWallet(backtestExecutionConfiguration.initialWalletAmount());
        this.wallet = () -> wallet;
        openPositions = new ArrayList<>();
        closedTrades = new ArrayList<>();
        this.pair = pair;
        tradingCalculator = new TradingCalculatorImpl(eurUsdRequestor);
        MarginCalculator marginCalculator = new MarginCalculatorImpl(eurUsdRequestor);
        positionCalculation = new PositionCalculation(tradingCalculator, marginCalculator);
        positionFactory = new DemoPositionFactory(eurUsdRequestor);
        positionAdaptor = new PositionAdaptor();
    }

    @Override
    protected TradeExecutor construct(BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair, EurUsdRequestor eurUsdRequestor) {
        return new DemoTradeExecutor(backtestExecutionConfiguration, pair, backtestExecutionConfiguration.getEurUsdRequestor());
    }

    @Override
    protected TradeExecutor construct(Pair pair, EurUsdRequestor eurUsdRequestor) {
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
        if (exitSignal.directionToClose() == Direction.BUY) {
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
        LevelEntrySignal levelEntrySignal = entrySignal.toLevelEntrySignal(tradingCalculator);
        Position position = toPosition(levelEntrySignal);
        PositionCalculationResult openPositionsResult = positionCalculation.openPosition(currentPrice, openPositions, position, wallet.get());
        openPositions = openPositionsResult.positions();
        closedTrades.addAll(openPositionsResult.trades());
    }

    private Position toPosition(LevelEntrySignal entrySignal) {
        return switch (entrySignal.positionType()) {
            case HARD_LIMIT -> positionFactory.createStopLimitPosition(entrySignal);
            case TRAILING -> positionFactory.createTrailingPosition(entrySignal);
        };
    }

    @Override
    public List<Trade> getAllClosedTrades() {
        return closedTrades;
    }

    @Override
    public void adaptPositions(TimeSeriesEntry currentPrice) {
        openPositions = positionAdaptor.adapt(currentPrice, openPositions);
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

    @Override
    public List<Trade> getTradesForDurationUntilNowForPair(Duration duration, Pair pair) {
        LocalDateTime startTime = LocalDateTime.now().minus(duration);
        return closedTrades.stream()
                .filter(trade -> trade.getCloseTime().isAfter(startTime))
                .toList();
    }

    @Override
    public List<Position> getOpenPositions() {
        return Collections.unmodifiableList(openPositions);
    }
}
