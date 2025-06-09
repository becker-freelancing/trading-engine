package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.BacktestResultReader;
import com.becker.freelance.backtest.resultviewer.app.metric.*;
import com.becker.freelance.backtest.util.PathUtil;
import com.becker.freelance.strategies.creation.StrategyCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class AbstractBacktestResultViewerApp implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBacktestResultViewerApp.class);

    private static final String RESULTS_DIR_NAME = "results\\";
    private static final String FILE_ENDING = ".zst";

    private static final List<MetricCalculator> ALL_METRICS = List.of(
            new AverageTradeHoldingDuration(),
            new NumberOfTrades(),
            new ProfitHitRatio(),
            new ProfitHitRatioWithoutFees(),
            new MaxDrawdownMetric(),
            new AverageProfitPerTrade(),
            new ReturnsStd(),
            new AverageOpenFeeRate(),
            new AverageCloseFeeRate(),
            new TotalFeeRate(),
            new MarketRegimeProfit(),
            new BuyProfitHitRate(),
            new SellProfitHitRate(),
            new DayOfWeekHitRate(),
            new HourOfDayHitRate()
    );


    public AbstractBacktestResultViewerApp() {

    }

    private static BacktestResultContent getBaseData(Path resultPath) {
        BacktestResultReader resultReader = new BacktestResultReader(resultPath);
        return resultReader.streamCsvContent().findAny().orElseThrow(() -> new IllegalStateException("No Results found"));
    }

    private static List<BacktestResultContent> getBestMin(Path resultPath) {
        BacktestResultReader resultReader = new BacktestResultReader(resultPath);
        return resultReader.streamCsvContentWithMinValue().toList();
    }

    private static List<BacktestResultContent> getBestCumulative(Path resultPath) {
        BacktestResultReader resultReader = new BacktestResultReader(resultPath);
        return resultReader.streamCsvContentWithCumulativeValue().toList();
    }

    private static List<BacktestResultContent> getBestMax(Path resultPath) {
        BacktestResultReader resultReader = new BacktestResultReader(resultPath);
        return resultReader.streamCsvContentWithMaxValue().toList();
    }

    private static Path askForResultFileName(StrategyCreator strategy) {
        String strategyResultDir = PathUtil.fromRelativePath(RESULTS_DIR_NAME + strategy.strategyName());
        List<Path> results;
        try (Stream<Path> walk = Files.walk(Path.of(strategyResultDir))) {
            results = walk.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(FILE_ENDING))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString())).toList();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read results from " + strategyResultDir);
        }

        return new PropertyAsker().askProperty(results, path -> path.getFileName().toString(), "Ergebnis");
    }

    private static StrategyCreator askForStrategy() {
        List<StrategyCreator> baseStrategies = StrategyCreator.findAll().stream()
                .filter(baseStrategy -> Files.exists(Path.of(PathUtil.fromRelativePath(RESULTS_DIR_NAME + baseStrategy.strategyName()))))
                .toList();
        return new PropertyAsker().askProperty(baseStrategies, StrategyCreator::strategyName, "Strategie");
    }

    @Override
    public void run() {
        Path resultPath = askForResultPath();

        logger.info("Reading Results from {}...", resultPath);
        logger.info("Reading Results finished");
        logger.info("Processing Results...");

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Future<List<BacktestResultContent>> bestMinFuture = executorService.submit(() -> getBestMin(resultPath));
        Future<List<BacktestResultContent>> bestCumulativeFuture = executorService.submit(() -> getBestCumulative(resultPath));
        Future<List<BacktestResultContent>> bestMaxFuture = executorService.submit(() -> getBestMax(resultPath));
        Future<BacktestResultContent> baseDataFuture = executorService.submit(() -> getBaseData(resultPath));

        List<BacktestResultContent> bestMin;
        List<BacktestResultContent> bestCumulative;
        List<BacktestResultContent> bestMax;
        BacktestResultContent baseData;
        try {
            bestMin = bestMinFuture.get();
            bestCumulative = bestCumulativeFuture.get();
            bestMax = bestMaxFuture.get();
            baseData = baseDataFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException("Could not read results", e);
        }
        executorService.shutdown();


        new BacktestResultConsoleWriter(bestCumulative, bestMax, bestMin, ALL_METRICS, baseData).run();
        new BacktestResultPlotter(bestCumulative, bestMax, bestMin).run();
    }

    private Path askForResultPath() {
        StrategyCreator strategy = askForStrategy();

        return askForResultFileName(strategy);
    }
}
