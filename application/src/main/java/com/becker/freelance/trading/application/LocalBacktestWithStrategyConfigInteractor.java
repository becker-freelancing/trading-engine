package com.becker.freelance.trading.application;

import com.becker.freelance.backtest.BacktestEngine;
import com.becker.freelance.backtest.StrategySupplierWithParameters;
import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.configuration.BacktestMode;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.trading.api.LocalBacktestPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class LocalBacktestWithStrategyConfigInteractor implements LocalBacktestPort {

    private static final Logger logger = LoggerFactory.getLogger(LocalBacktestWithStrategyConfigInteractor.class);

    private final Decimal initialWalletAmount;
    private final LocalDateTime fromTime;
    private final LocalDateTime toTime;
    private final Runnable onFinished;
    private final StrategyCreator strategy;
    private final AppMode appMode;
    private final List<Pair> pairs;
    private final Integer numThreads;
    private final Integer parameterLimit;
    private final List<StrategySupplierWithParameters> strategySuppliers;
    private final BacktestMode backtestMode;

    public LocalBacktestWithStrategyConfigInteractor(Decimal initialWalletAmount, LocalDateTime fromTime, LocalDateTime toTime, Runnable onFinished, StrategyCreator strategy, AppMode appMode, List<Pair> pairs, Integer numThreads, Integer parameterLimit, List<StrategySupplierWithParameters> strategySuppliers, BacktestMode backtestMode) {
        this.initialWalletAmount = initialWalletAmount;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.onFinished = onFinished;
        this.strategy = strategy;
        this.appMode = appMode;
        this.pairs = pairs;
        this.numThreads = numThreads;
        this.parameterLimit = parameterLimit;
        this.strategySuppliers = strategySuppliers;
        this.backtestMode = backtestMode;
    }

    @Override
    public void run() {

        AppConfiguration appConfiguration = new AppConfiguration(appMode, LocalDateTime.now());
        BacktestExecutionConfiguration backtestExecutionConfiguration = new BacktestExecutionConfiguration(pairs, initialWalletAmount, fromTime, toTime, numThreads, parameterLimit, backtestMode);


        runWithStrategyConfig(appConfiguration, backtestExecutionConfiguration, strategy, onFinished);
    }

    private void runWithStrategyConfig(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, StrategyCreator strategy, Runnable onFinished) {

        logger.info("\t\tAnzahl Permutationen {}", strategySuppliers.size());

        BacktestEngine backtestEngine = new BacktestEngine(appConfiguration, backtestExecutionConfiguration, strategySuppliers, onFinished, strategy.strategyName());
        backtestEngine.run();
    }
}
