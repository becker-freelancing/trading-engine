package com.becker.freelance.app;

import com.becker.freelance.app.BacktestAppInitiatingUtil.LastExecutionProperties;
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
import java.util.Optional;

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
        Optional<LastExecutionProperties> properties = appInitiatingUtil.findProperties();
        BaseStrategy strategy;
        AppMode appMode;
        List<Pair> pairs;
        Integer numThreads;
        if (properties.isPresent()) {
            LastExecutionProperties lastExecutionProperties = properties.get();
            strategy = lastExecutionProperties.baseStrategy();
            appMode = lastExecutionProperties.appMode();
            pairs = lastExecutionProperties.pairs();
            numThreads = lastExecutionProperties.numberOfThread();
        } else {
            strategy = appInitiatingUtil.askStrategy();
            appMode = appInitiatingUtil.askAppMode();
            pairs = appInitiatingUtil.askPair(appMode);
            numThreads = appInitiatingUtil.askNumberOfThreads();
        }
        appInitiatingUtil.saveProperties(strategy, numThreads, pairs, appMode);
        logger.info("\t\tAnzahl Permutationen {}", strategy.getParameters().permutate().size());

        TimeSeries eurusd = DataProviderFactory.find(appMode).createDataProvider(Pair.eurUsd1()).readTimeSeries(fromTime.minusDays(1), toTime);

        AppConfiguration appConfiguration = new AppConfiguration(appMode, LocalDateTime.now());
        BacktestExecutionConfiguration backtestExecutionConfiguration = new BacktestExecutionConfiguration(pairs, initialWalletAmount, eurusd, fromTime, toTime, numThreads);

        BacktestEngine backtestEngine = new BacktestEngine(appConfiguration, backtestExecutionConfiguration, strategy);
        backtestEngine.run();
    }

}
