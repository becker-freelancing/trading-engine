package com.becker.freelance.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Trade;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.opentrades.OpenPositionRequestor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public abstract class TradeExecutor implements OpenPositionRequestor {

    public static TradeExecutor find(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration) {
        List<TradeExecutor> tradeExecutorsForPairs = backtestExecutionConfiguration.pairs().stream()
                .map(pair -> TradeExecutor.findForPair(appConfiguration, backtestExecutionConfiguration, pair))
                .toList();

        return new MultiplePairTradeExecutor(tradeExecutorsForPairs, backtestExecutionConfiguration);
    }

    public static TradeExecutor find(AppConfiguration appConfiguration, Pair pair) {
        ServiceLoader<TradeExecutor> tradeExecutors = ServiceLoader.load(TradeExecutor.class);
        AppMode appMode = appConfiguration.appMode();
        List<TradeExecutor> executors = tradeExecutors.stream().map(ServiceLoader.Provider::get).filter(provider -> provider.supports(appMode)).toList();

        if (executors.size() > 1) {
            throw new IllegalStateException("Found multiple TradeExecutor for AppMode " + appMode.getDescription() + ": " + executors);
        }
        if (executors.isEmpty()) {
            throw new IllegalArgumentException("AppMode " + appMode.getDescription() + " is not supported");
        }

        return executors.get(0).construct(pair);
    }

    private static TradeExecutor findForPair(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair) {
        ServiceLoader<TradeExecutor> tradeExecutors = ServiceLoader.load(TradeExecutor.class);
        AppMode appMode = appConfiguration.appMode();
        List<TradeExecutor> executors = tradeExecutors.stream().map(ServiceLoader.Provider::get).filter(provider -> provider.supports(appMode)).toList();

        if (executors.size() > 1) {
            throw new IllegalStateException("Found multiple TradeExecutor for AppMode " + appMode.getDescription() + ": " + executors);
        }
        if (executors.isEmpty()) {
            throw new IllegalArgumentException("AppMode " + appMode.getDescription() + " is not supported");
        }

        return executors.get(0).construct(backtestExecutionConfiguration, pair);
     }

    protected abstract TradeExecutor construct(Pair pair);

    protected abstract TradeExecutor construct(BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair);

    protected abstract boolean supports(AppMode appMode);

    public abstract void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice);

    public abstract void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal);

    public abstract void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal);

    public abstract List<Trade> getAllClosedTrades();

    public abstract BacktestWallet getWallet();

    public abstract void adaptPositions(TimeSeriesEntry currentPrice);

    protected abstract Pair getPair();

    protected abstract void setWallet(Supplier<BacktestWallet> wallet);
}
