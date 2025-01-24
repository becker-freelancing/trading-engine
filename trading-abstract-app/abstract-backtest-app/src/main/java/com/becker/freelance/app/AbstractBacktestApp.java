package com.becker.freelance.app;

import com.becker.freelance.backtest.BacktestEngine;
import com.becker.freelance.commons.*;
import com.becker.freelance.data.DataProvider;
import com.becker.freelance.strategies.BaseStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AbstractBacktestApp {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBacktestApp.class);

    public static final Double INITIAL_WALLET_AMOUNT = 2000.;
    public static final LocalDateTime FROM_TIME = LocalDateTime.parse("2023-06-01T00:00:00");
    public static final LocalDateTime TO_TIME = LocalDateTime.parse("2024-01-01T00:00:00");


    public static void main(String[] args) throws IOException {
        PropertyAsker propertyAsker = new PropertyAsker();
        BaseStrategy strategy = propertyAsker.askProperty(BaseStrategy.loadAll(), BaseStrategy::getName, "Strategie");
        logger.info("\t\tAnzahl Permutationen {}", strategy.getParameters().permutate().size());
        AppMode appMode = propertyAsker.askProperty(AppMode.findAll(), AppMode::getDescription, "AppMode");
        Pair pair = propertyAsker.askProperty(List.of(Pair.values()), Pair::getTechnicalName, "Pair");
        Integer numThreads = propertyAsker.askProperty(List.of(1, 20, 40, 80), i -> Integer.toString(i), "Anzahl an Threads");

        TimeSeries eurusd = DataProvider.getInstance(appMode).readTimeSeries(Pair.EURUSD_1, FROM_TIME.minusDays(1), TO_TIME);


        AppConfiguration appConfiguration = new AppConfiguration(appMode, numThreads, LocalDateTime.now());
        ExecutionConfiguration executionConfiguration = new ExecutionConfiguration(pair, INITIAL_WALLET_AMOUNT, eurusd, FROM_TIME, TO_TIME);

        BacktestEngine backtestEngine = new BacktestEngine(appConfiguration, executionConfiguration, strategy);
        backtestEngine.run();

    }

}
