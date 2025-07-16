package com.becker.freelance.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.bybit.trades.TradeController;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.calculation.TradingCalculatorImpl;
import com.becker.freelance.commons.calculation.TradingFeeCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.trade.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class BybitTradeExecutor extends TradeExecutor {

    private static final Logger logger = LoggerFactory.getLogger(BybitTradeExecutor.class);
    private static final Duration MAX_ENTRY_SIGNAL_AGE = Duration.ofSeconds(5);
    private static final Duration MAX_OPEN_ORDER_AGE = Duration.ofMinutes(1);

    private TradeController tradeController;
    private TradingCalculator tradingCalculator;
    private TradingFeeCalculator tradingFeeCalculator;

    public BybitTradeExecutor() {
    }

    public BybitTradeExecutor(Pair pair, EurUsdRequestor eurUsdRequestor) {
        tradeController = new TradeController();
        tradingCalculator = new TradingCalculatorImpl(eurUsdRequestor);
        tradingFeeCalculator = TradingFeeCalculator.getInstance();
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
        return false;
//        return appMode.isDemo() && appMode.getDataSourceName().equals("BYBIT_REMOTE");
    }

    @Override
    public void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice) {
//Not needed -> Only for local backtest
    }

    @Override
    public void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal) {

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal) {
        long durationSinceNewEntry = ChronoUnit.SECONDS.between(time, LocalDateTime.now());
//        if (MAX_ENTRY_SIGNAL_AGE.toSeconds() < durationSinceNewEntry) {
//            logger.warn("Rejected Entry Signal with age {} seconds. Max Age of Entry Signal is {}", durationSinceNewEntry, MAX_ENTRY_SIGNAL_AGE.toSeconds());
//            return;
//        }
        logger.debug("Executing Entry Signal {}", entrySignal);
        tradeController.entry(entrySignal);
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

    @Override
    public List<Position> getOpenPositions() {
        return Collections.unmodifiableList(tradeController.allPositions());
    }

    @Override
    public List<Trade> getTradesForDurationUntilTimeForPair(LocalDateTime toTime, Duration duration, Pair pair) {
        return tradeController.getTradesForDurationUntilNowForPair(toTime, duration, pair);
    }
}
