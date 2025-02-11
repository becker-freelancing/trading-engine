package com.becker.freelance.app;

import com.becker.freelance.backtest.BacktestEngine;
import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.ExecutionConfiguration;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.data.DataProvider;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.BaseStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

class AbstractBacktestApp implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(AbstractBacktestApp.class);


    private final Decimal initialWalletAmount;
    private final LocalDateTime fromTime;
    private final LocalDateTime toTime;
    private final BacktestAppInitiatingUtil appInitiatingUtil;

    AbstractBacktestApp(Decimal initialWalletAmount, LocalDateTime fromTime, LocalDateTime toTime) {
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
        Pair pair = appInitiatingUtil.askPair(appMode);
        Integer numThreads = appInitiatingUtil.askNumberOfThreads();

        TimeSeries eurusd = null;
        try {
            eurusd = DataProvider.getInstance(appMode).readTimeSeries(Pair.eurUsd1(), fromTime.minusDays(1), toTime);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read Time Series EUR/USD M1", e);
        }


        AppConfiguration appConfiguration = new AppConfiguration(appMode, numThreads, LocalDateTime.now());
        ExecutionConfiguration executionConfiguration = new ExecutionConfiguration(pair, initialWalletAmount, eurusd, fromTime, toTime);

        BacktestEngine backtestEngine = new BacktestEngine(appConfiguration, executionConfiguration, strategy);
        backtestEngine.run();
    }

}
