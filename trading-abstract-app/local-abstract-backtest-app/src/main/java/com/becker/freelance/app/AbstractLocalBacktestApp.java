package com.becker.freelance.app;

import com.becker.freelance.backtest.BacktestEngine;
import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.data.DataProviderFactory;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.BaseStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

class AbstractLocalBacktestApp implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLocalBacktestApp.class);


    private final Decimal initialWalletAmount;
    private final LocalDateTime fromTime;
    private final LocalDateTime toTime;
    private final BacktestAppInitiatingUtil appInitiatingUtil;

    AbstractLocalBacktestApp(Decimal initialWalletAmount, LocalDateTime fromTime, LocalDateTime toTime) {
        this.initialWalletAmount = initialWalletAmount;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.appInitiatingUtil = new BacktestAppInitiatingUtil();
    }

    @Override
    public void run() {
        BaseStrategy strategy = appInitiatingUtil.askStrategy();
        logger.info("\t\tAnzahl Permutationen {}", strategy.getParameters().permutate().size());
        AppMode appMode = appInitiatingUtil.askAppMode();
        List<Pair> pairs = appInitiatingUtil.askPair(appMode);
        Integer numThreads = appInitiatingUtil.askNumberOfThreads();

        TimeSeries eurusd = DataProviderFactory.find(appMode).createDataProvider(Pair.eurUsd1()).readTimeSeries(fromTime.minusDays(1), toTime);

        AppConfiguration appConfiguration = new AppConfiguration(appMode, LocalDateTime.now());
        BacktestExecutionConfiguration backtestExecutionConfiguration = new BacktestExecutionConfiguration(pairs, initialWalletAmount, eurusd, fromTime, toTime, numThreads);

        BacktestEngine backtestEngine = new BacktestEngine(appConfiguration, backtestExecutionConfiguration, strategy);
        backtestEngine.run();
    }

}
