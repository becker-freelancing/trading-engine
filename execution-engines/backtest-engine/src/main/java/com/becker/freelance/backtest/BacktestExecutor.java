package com.becker.freelance.backtest;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.data.DataProviderFactory;
import com.becker.freelance.data.SubscribableDataProvider;
import com.becker.freelance.engine.StrategyEngine;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.management.api.environment.TimeChangeListener;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;
import com.becker.freelance.tradeexecution.TradeExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BacktestExecutor implements Runnable {

    private final AppConfiguration appConfiguration;
    private final BacktestExecutionConfiguration backtestExecutionConfiguration;
    private final BiConsumer<List<Trade>, StrategyCreationParameter> onBacktestFinished;
    private final Consumer<Exception> onError;
    private final StrategyCreationParameter parameters;
    private final StrategySupplier strategySupplier;


    public BacktestExecutor(AppConfiguration appConfiguration,
                            BacktestExecutionConfiguration backtestExecutionConfiguration,
                            BiConsumer<List<Trade>, StrategyCreationParameter> onBacktestFinished,
                            Consumer<Exception> onError,
                            StrategyCreationParameter parameters,
                            StrategySupplier strategySupplier) {
        this.appConfiguration = appConfiguration;
        this.backtestExecutionConfiguration = backtestExecutionConfiguration;
        this.onBacktestFinished = onBacktestFinished;
        this.onError = onError;
        this.parameters = parameters;
        this.strategySupplier = strategySupplier;
    }

    @Override
    public void run() {
        try {
            TradeExecutor tradeExecutor = TradeExecutor.find(appConfiguration, backtestExecutionConfiguration);

            DataProviderFactory dataProviderFactory = DataProviderFactory.find(appConfiguration.appMode());

            LocalDateTime minTime = backtestExecutionConfiguration.startTime();
            LocalDateTime maxTime = backtestExecutionConfiguration.endTime();
            BacktestSynchronizer backtestSynchronizer = new BacktestSynchronizer(minTime, maxTime);

            for (Pair pair : backtestExecutionConfiguration.pairs()) {
                SubscribableDataProvider dataProviderForPair = dataProviderFactory.createSubscribableDataProvider(pair, backtestSynchronizer);
                Consumer<TimeChangeListener> timeChangeListenerConsumer = listener -> backtestSynchronizer.addPrioritySubscribor(new TimeChangeListenerSynchronizeable(listener));
                StrategyEngine strategyEngine = new StrategyEngine(pair,
                        strategySupplier,
                        tradeExecutor,
                        backtestExecutionConfiguration.getEurUsdRequestor(),
                        dataProviderForPair,
                        timeChangeListenerConsumer,
                        (strategy, time) -> {
                        });
                StrategyDataSubscriber strategyDataSubscriber = new StrategyDataSubscriber(strategyEngine);
                dataProviderForPair.addSubscriber(strategyDataSubscriber);
            }

            while (backtestSynchronizer.getCurrentTime().isBefore(maxTime)) {
                backtestSynchronizer.shiftOneMinute();
            }

            List<Trade> allClosedTrades = tradeExecutor.getAllClosedTrades();
            onBacktestFinished.accept(allClosedTrades, parameters);
        } catch (Exception e) {
            onError.accept(e);
        }
    }

    public StrategyCreationParameter getParameter() {
        return parameters;
    }
}
