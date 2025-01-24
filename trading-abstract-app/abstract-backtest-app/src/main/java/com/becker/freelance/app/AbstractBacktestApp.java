package com.becker.freelance.app;

import com.becker.freelance.backtest.BacktestEngine;
import com.becker.freelance.commons.*;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.data.DataProvider;
import com.becker.freelance.strategies.BaseStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AbstractBacktestApp implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(AbstractBacktestApp.class);

    public static AbstractBacktestAppBuilder builder(){
        return new AbstractBacktestAppBuilder();
    }

    private final Double initialWalletAmount;
    private final LocalDateTime fromTime;
    private final LocalDateTime toTime;

    AbstractBacktestApp(Double initialWalletAmount, LocalDateTime fromTime, LocalDateTime toTime) {
        this.initialWalletAmount = initialWalletAmount;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    @Override
    public void run() {
        PropertyAsker propertyAsker = new PropertyAsker();
        BaseStrategy strategy = propertyAsker.askProperty(BaseStrategy.loadAll(), BaseStrategy::getName, "Strategie");
        logger.info("\t\tAnzahl Permutationen {}", strategy.getParameters().permutate().size());
        AppMode appMode = propertyAsker.askProperty(AppMode.findAll(), AppMode::getDescription, "AppMode");
        Pair pair = propertyAsker.askProperty(Pair.allPairs(), Pair::technicalName, "Pair");
        Integer numThreads = propertyAsker.askProperty(List.of(1, 20, 40, 80), i -> Integer.toString(i), "Anzahl an Threads");

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
