package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultReader;
import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.backtest.resultviewer.app.callback.ParsedCallback;
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
import java.util.Map;
import java.util.stream.Stream;

public class AbstractBacktestResultViewerApp implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBacktestResultViewerApp.class);

    private static final String RESULTS_DIR_NAME = "results/";
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

    private final ParsedCallback parsedBacktestResultConsumer;


    public AbstractBacktestResultViewerApp() {
        this(ParsedCallback.noop());
    }

    public AbstractBacktestResultViewerApp(ParsedCallback parsedBacktestResultConsumer) {
        this.parsedBacktestResultConsumer = parsedBacktestResultConsumer;
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

    private static ResultParser askForResultParser() {
        Map<ResultParser, String> resultParser = Map.of(
                new WithoutRegimeResultParser(), "Ohne Regime-Betrachtung",
                new WithRegimeResultParser(), "Mit Regime-Betrachtung"
        );

        List<ResultParser> selections = resultParser.keySet().stream()
                .sorted(Comparator.comparing(resultParser::get))
                .toList();
        return new PropertyAsker().askProperty(selections,
                resultParser::get, "Ergebnis-Verarbeiter");
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
        String strategyName = resultPath.getFileName().toString().split("\\.")[0];
        ResultParser resultParser = askForResultParser();

        logger.info("Reading Results from {}...", resultPath);

        Runnable onFinish = () -> {
            logger.info("Reading Results finished");
            logger.info("Processing Results...");

            resultParser.run(ALL_METRICS, strategyName, parsedBacktestResultConsumer, resultPath);
        };

        BacktestResultReader backtestResultReader = new BacktestResultReader(resultPath);
        backtestResultReader.readCsvContent(resultPath,
                onFinish,
                resultParser.getResultExtractors().toArray(new ResultExtractor[0]));

    }

    private Path askForResultPath() {
        StrategyCreator strategy = askForStrategy();

        return askForResultFileName(strategy);
    }
}
