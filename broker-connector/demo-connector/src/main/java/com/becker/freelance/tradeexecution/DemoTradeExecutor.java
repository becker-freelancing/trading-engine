package com.becker.freelance.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.broker.DemoBrokerRequestor;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.calculation.TradingFeeCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionFactory;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.tradeexecution.calculation.MarginCalculatorImpl;
import com.becker.freelance.tradeexecution.calculation.PositionCalculation;
import com.becker.freelance.tradeexecution.calculation.PositionCalculation.PositionCalculationResult;
import com.becker.freelance.tradeexecution.calculation.TrailingPositionAdaptor;
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
    private ClosedTradesHolder closedTrades;
    private PositionCalculation positionCalculation;
    private Pair pair;
    private PositionFactory positionFactory;
    private TrailingPositionAdaptor trailingPositionAdaptor;
    private List<Position> positionsToExecuteBuffer;

    public DemoTradeExecutor(){

    }

    private DemoTradeExecutor(BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair, EurUsdRequestor eurUsdRequestor) {
        BacktestWallet wallet = new BacktestWallet(backtestExecutionConfiguration.initialWalletAmount());
        this.wallet = () -> wallet;
        openPositions = new ArrayList<>();
        closedTrades = new ClosedTradesHolder();
        this.pair = pair;
        TradingCalculator tradingCalculator = new DemoBrokerRequestor().getTradingCalculator(eurUsdRequestor);
        TradingFeeCalculator tradingFeeCalculator = TradingFeeCalculator.getInstance();
        positionCalculation = new PositionCalculation(tradingCalculator, tradingFeeCalculator);
        positionFactory = new DemoPositionFactory(eurUsdRequestor, tradingFeeCalculator, new MarginCalculatorImpl(eurUsdRequestor));
        trailingPositionAdaptor = new TrailingPositionAdaptor();
        positionsToExecuteBuffer = new ArrayList<>();
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
        Position position = toPosition(entrySignal, currentPrice);
        if (position.getOpenOrder().canBeExecuted(currentPrice)) {
            internalEntry(currentPrice, position);
        } else {
            positionsToExecuteBuffer.add(position);
        }
    }

    private void internalEntry(TimeSeriesEntry currentPrice, Position position) {
        position.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult openPositionsResult = positionCalculation.openPosition(currentPrice, openPositions, position, wallet.get());

        openPositions = openPositionsResult.positions();
        closedTrades.addAll(openPositionsResult.trades());
    }

    private Position toPosition(EntrySignal entrySignal, TimeSeriesEntry currentPrice) {
        return switch (entrySignal.getPositionBehaviour()) {
            case HARD_LIMIT -> positionFactory.createStopLimitPosition(entrySignal);
            case TRAILING -> positionFactory.createTrailingPosition(entrySignal, currentPrice);
        };
    }

    @Override
    public List<Trade> getAllClosedTrades() {
        return closedTrades.toList();
    }

    @Override
    public void adaptPositions(TimeSeriesEntry currentPrice) {
        // Try Execution Pending Positions wich are not opened, e.g. due to Limit Open Orders
        for (Position positionToExecute : positionsToExecuteBuffer) {
            if (positionToExecute.getOpenOrder().canBeExecuted(currentPrice)) {
                internalEntry(currentPrice, positionToExecute);
            }
        }
        this.positionsToExecuteBuffer = new ArrayList<>();


        // Adapt Trailing Positions
        openPositions = trailingPositionAdaptor.adapt(currentPrice, openPositions);
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
    public List<Trade> getTradesForDurationUntilTimeForPair(LocalDateTime toTime, Duration duration, Pair pair) {
        LocalDateTime startTime = toTime.minus(duration);
        return new ArrayList<>(closedTrades.getTradesInRange(startTime, toTime));
    }

    @Override
    public List<Position> getOpenPositions() {
        return Collections.unmodifiableList(openPositions);
    }
}
