package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;
import com.becker.freelance.commons.pair.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StrategyConfigConsoleWriter implements ResultVisualizer {

    private static final Logger logger = LoggerFactory.getLogger(StrategyConfigConsoleWriter.class);

    private static final String TEMPLATE = """
            {
                "strategyName": "%s",
                "priority": 100,
                "pair": "%s",
                "regimes": [
                    "%s"
                ],
                "parameter": %s
            }
            """;

    @Override
    public void visualize(String strategyName, BacktestResultContent baseData, List<BacktestResultContent> bestCumulative, List<BacktestResultContent> bestMax, List<BacktestResultContent> bestMin, List<BacktestResultContent> mostTrades, List<MetricCalculator> metrics) {
        if (bestCumulative.size() == 0) {
            logger.warn("No Best Cumulative found for writing strategy config.");
            return;
        }

        String strategyConfig = buildStrategyConfig(strategyName, bestCumulative.get(0));
        logger.info("\n{}", strategyConfig);
    }

    private String buildStrategyConfig(String strategyName, BacktestResultContent backtestResultContent) {
        String parametersJson = backtestResultContent.parametersJson();
        String[] split = parametersJson.split("\n");
        if (split.length > 1) {
            return backtestResultContent.parsePairs().stream()
                    .map(pair -> buildStrategyConfigForRegimes(strategyName, pair, split))
                    .collect(Collectors.joining(",\n"));
        }

        throw new UnsupportedOperationException("Not implemented yet. => Only supported for by regime analysis");
    }

    private String buildStrategyConfigForRegimes(String strategyName, Pair pair, String[] split) {
        return Arrays.stream(split)
                .filter(line -> line.contains("\""))
                .map(regimeAndParameter -> buildStrategyConfigForRegime(strategyName, pair, regimeAndParameter))
                .collect(Collectors.joining(",\n"));
    }

    private String buildStrategyConfigForRegime(String strategyName, Pair pair, String regimeAndParameter) {
        String regime = regimeAndParameter.substring(1, regimeAndParameter.indexOf("\"", 1));
        String parameters = regimeAndParameter.substring(regimeAndParameter.indexOf("{"));

        return String.format(TEMPLATE,
                parseStrategyName(strategyName),
                pair.shortName(),
                regime,
                parameters
        );
    }

    private String parseStrategyName(String strategyName) {
        int first = strategyName.indexOf("__");
        return strategyName.substring(first + 2, strategyName.indexOf("__", first + 1));
    }
}
