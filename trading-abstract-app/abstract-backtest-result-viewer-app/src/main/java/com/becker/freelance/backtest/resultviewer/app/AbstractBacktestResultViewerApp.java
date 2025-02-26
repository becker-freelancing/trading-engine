package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.BacktestResultReader;
import com.becker.freelance.commons.PathUtil;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.BaseStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractBacktestResultViewerApp implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBacktestResultViewerApp.class);

    private static final String RESULTS_DIR_NAME = "results\\";
    private static final String FILE_ENDING = ".zst";


    public AbstractBacktestResultViewerApp() {

    }

    @Override
    public void run() {
        Path resultPath = askForResultPath();

        logger.info("Reading Results from {}...", resultPath);
        logger.info("Reading Results finished");
        logger.info("Processing Results...");

        Set<BacktestResultContent> bestMin = getBestMin(resultPath);
        Set<BacktestResultContent> bestCumulative = getBestCumulative(resultPath);
        Set<BacktestResultContent> bestMax = getBestMax(resultPath);
        BacktestResultContent baseData = getBaseData(resultPath);



        new BacktestResultConsoleWriter(bestCumulative, bestMax, bestMin, baseData).run();
        new BacktestResultPlotter(bestCumulative, bestMax, bestMin).run();
    }

    private static BacktestResultContent getBaseData(Path resultPath) {
        BacktestResultReader resultReader = new BacktestResultReader(resultPath);
        BacktestResultContent baseData = resultReader.streamCsvContent().findAny().orElseThrow(() -> new IllegalStateException("No Results found"));
        return baseData;
    }

    private static Set<BacktestResultContent> getBestMin(Path resultPath) {
        BacktestResultReader resultReader = new BacktestResultReader(resultPath);
        Decimal bestMinValue = resultReader.streamMinValues().max(Comparator.naturalOrder()).orElse(Decimal.ZERO);
        Set<BacktestResultContent> bestMin = resultReader.streamCsvContentWithMinValue(bestMinValue).collect(Collectors.toSet());
        return bestMin;
    }

    private static Set<BacktestResultContent> getBestCumulative(Path resultPath) {
        BacktestResultReader resultReader = new BacktestResultReader(resultPath);
        Decimal bestCumulativeValue = resultReader.streamCumulativeValues().max(Comparator.naturalOrder()).orElse(Decimal.ZERO);
        Set<BacktestResultContent> bestCumulative = resultReader.streamCsvContentWithCumulativeValue(bestCumulativeValue).collect(Collectors.toSet());
        return bestCumulative;
    }

    private static Set<BacktestResultContent> getBestMax(Path resultPath) {
        BacktestResultReader resultReader = new BacktestResultReader(resultPath);
        Decimal bestMaxValue = resultReader.streamMaxValues().max(Comparator.naturalOrder()).orElse(Decimal.ZERO);
        Set<BacktestResultContent> bestMax = resultReader.streamCsvContentWithMaxValue(bestMaxValue).collect(Collectors.toSet());
        return bestMax;
    }


    private Set<BacktestResultContent> filterForBest(Stream<BacktestResultContent> backtestResultContents, Function<BacktestResultContent, Decimal> extractor) {
        Stream.Builder<BacktestResultContent> findMaxStream = Stream.builder();
        Stream.Builder<BacktestResultContent> findMaxElementsStream = Stream.builder();
        backtestResultContents.forEach(result -> {
            findMaxStream.add(result);
            findMaxElementsStream.add(result);
        });
        Decimal best = findMaxStream.build().map(extractor).max(Comparator.naturalOrder()).orElse(Decimal.ZERO);
        return findMaxElementsStream.build().filter(result -> Objects.equals(extractor.apply(result), best)).collect(Collectors.toSet());
    }




    private Path askForResultPath() {
        BaseStrategy strategy = askForStrategy();

        return askForResultFileName(strategy);
    }

    private static Path askForResultFileName(BaseStrategy strategy) {
        String strategyResultDir = PathUtil.fromRelativePath(RESULTS_DIR_NAME + strategy.getName());
        List<Path> results;
        try (Stream<Path> walk = Files.walk(Path.of(strategyResultDir))) {
            results = walk.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(FILE_ENDING))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString())).toList();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read results from " + strategyResultDir);
        }

        Path result = new PropertyAsker().askProperty(results, path -> path.getFileName().toString(), "Ergebnis");
        return result;
    }

    private static BaseStrategy askForStrategy() {
        List<BaseStrategy> baseStrategies = BaseStrategy.loadAll().stream()
                .filter(baseStrategy -> Files.exists(Path.of(PathUtil.fromRelativePath(RESULTS_DIR_NAME + baseStrategy.getName()))))
                .toList();
        BaseStrategy strategy = new PropertyAsker().askProperty(baseStrategies, BaseStrategy::getName, "Strategie");
        return strategy;
    }
}
