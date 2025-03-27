package com.becker.freelance.app;

import com.becker.freelance.backtest.BacktestEngine;
import com.becker.freelance.backtest.ExcludeExistingParametersFilter;
import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.BacktestResultReader;
import com.becker.freelance.backtest.commons.BacktestResultZipper;
import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.util.PathUtil;
import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.data.DataProviderFactory;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.BaseStrategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AbstractLocalBacktestContinueApp implements Runnable {

    private final BacktestAppInitiatingUtil appInitiatingUtil;
    private Decimal initialWalletAmount;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;
    private AppMode appMode;
    private List<Pair> pairs;

    AbstractLocalBacktestContinueApp() {
        this.appInitiatingUtil = new BacktestAppInitiatingUtil();
    }

    @Override
    public void run() {
        PropertyAsker propertyAsker = new PropertyAsker();
        BaseStrategy strategy = appInitiatingUtil.askStrategy();
        List<Path> strategyResults;
        try (Stream<Path> walk = Files.walk(Path.of(PathUtil.resultDirForStrategy(strategy.getName())))){
            strategyResults = walk.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".zst")).sorted(Comparator.comparing(path -> path.getFileName().toString())).toList();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read results for strategy " + strategy.getName(), e);
        }
        Path resultPath = propertyAsker.askProperty(strategyResults, path -> path.getFileName().toString(), "Ergebnis");
        Integer numThreads = appInitiatingUtil.askNumberOfThreads();
        Set<BacktestResultContent> backtestResultContents = new BacktestResultReader(resultPath).readCsvContent();
        parseAppParameter(backtestResultContents);
        Path resultWriteFile = unzipResultFile(resultPath);
        Set<Map<String, Decimal>> parameters = backtestResultContents.stream().map(BacktestResultContent::parameters).collect(Collectors.toSet());

        TimeSeries eurusd = readEurUsdTimeSeries(appMode);


        AppConfiguration appConfiguration = new AppConfiguration(appMode, LocalDateTime.now());
        BacktestExecutionConfiguration backtestExecutionConfiguration = new BacktestExecutionConfiguration(pairs, initialWalletAmount, eurusd, fromTime, toTime, numThreads);

        BacktestEngine backtestEngine = new BacktestEngine(appConfiguration, backtestExecutionConfiguration, strategy, new ExcludeExistingParametersFilter(parameters), resultWriteFile);
        backtestEngine.run();
    }

    private Path unzipResultFile(Path resultPath) {
        return new BacktestResultZipper(resultPath).unzipFile();
    }

    private void parseAppParameter(Set<BacktestResultContent> backtestResultContents) {
        BacktestResultContent result = backtestResultContents.stream().findAny().orElseThrow(() -> new IllegalStateException("No Results found"));

        fromTime = result.fromTime();
        toTime = result.toTime();
        initialWalletAmount = result.initialWalletAmount();
        pairs = result.parsePairs();
        appMode = AppMode.fromDescription(result.appMode());
    }

    private TimeSeries readEurUsdTimeSeries(AppMode appMode) {

        return DataProviderFactory.find(appMode).createDataProvider(Pair.eurUsd1()).readTimeSeries(fromTime.minusDays(1), toTime);
    }
}
