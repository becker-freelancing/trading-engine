package com.becker.freelance.tradeexecution;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.service.ExtServiceLoader;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.commons.wallet.Wallet;
import com.becker.freelance.opentrades.ClosedTradesRequestor;
import com.becker.freelance.opentrades.OpenPositionRequestor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class TradeExecutor implements OpenPositionRequestor, ClosedTradesRequestor {

    public static TradeExecutor find(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration) {
        List<TradeExecutor> tradeExecutorsForPairs = backtestExecutionConfiguration.pairs().stream()
                .map(pair -> TradeExecutor.findForPair(appConfiguration, backtestExecutionConfiguration, pair))
                .toList();

        return new MultiplePairTradeExecutor(tradeExecutorsForPairs, backtestExecutionConfiguration);
    }

    public static TradeExecutor find(AppConfiguration appConfiguration, Pair pair, EurUsdRequestor eurUsdRequestor) {
        AppMode appMode = appConfiguration.appMode();
        List<TradeExecutor> executors = ExtServiceLoader.loadMultiple(TradeExecutor.class)
                .filter(provider -> provider.supports(appMode)).toList();

        if (executors.size() > 1) {
            throw new IllegalStateException("Found multiple TradeExecutor for AppMode " + appMode.getDescription() + ": " + executors);
        }
        if (executors.isEmpty()) {
            throw new IllegalArgumentException("AppMode " + appMode.getDescription() + " is not supported");
        }

        return executors.get(0).construct(pair, eurUsdRequestor);
    }

    private static TradeExecutor findForPair(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair) {
        AppMode appMode = appConfiguration.appMode();
        List<TradeExecutor> executors = ExtServiceLoader.loadMultiple(TradeExecutor.class)
                .filter(provider -> provider.supports(appMode)).toList();

        if (executors.size() > 1) {
            throw new IllegalStateException("Found multiple TradeExecutor for AppMode " + appMode.getDescription() + ": " + executors);
        }
        if (executors.isEmpty()) {
            throw new IllegalArgumentException("AppMode " + appMode.getDescription() + " is not supported");
        }

        return executors.get(0).construct(backtestExecutionConfiguration, pair, backtestExecutionConfiguration.getEurUsdRequestor());
     }

    protected abstract TradeExecutor construct(Pair pair, EurUsdRequestor eurUsdRequestor);

    protected abstract TradeExecutor construct(BacktestExecutionConfiguration backtestExecutionConfiguration, Pair pair, EurUsdRequestor eurUsdRequestor);

    protected abstract boolean supports(AppMode appMode);

    public abstract void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice);

    public abstract void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal);

    public abstract void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal);

    public abstract List<Trade> getAllClosedTrades();

    public abstract void adaptPositions(TimeSeriesEntry currentPrice);

    protected abstract Pair getPair();

    protected abstract void setWallet(Supplier<BacktestWallet> wallet);

    public Optional<Wallet> getWallet() {
        return Optional.empty();
    }
}
