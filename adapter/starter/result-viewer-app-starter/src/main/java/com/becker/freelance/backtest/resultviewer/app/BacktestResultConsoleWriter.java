package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;
import com.becker.freelance.backtest.resultviewer.app.metric.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

class BacktestResultConsoleWriter implements ResultVisualizer {

    private static final Logger logger = LoggerFactory.getLogger(BacktestResultConsoleWriter.class);

    @Override
    public void visualize(String strategyName, BacktestResultContent baseData, List<BacktestResultContent> bestCumulative, List<BacktestResultContent> bestMax, List<BacktestResultContent> bestMin, List<BacktestResultContent> mostTrades, List<MetricCalculator> metrics) {
        displayBaseData(baseData);
        displayResults(bestCumulative, "Bestes Kumulatives Ergebnisse", metrics);
        displayResults(bestMax, "Bestes Maximales Ergebnisse", metrics);
        displayResults(bestMin, "Bestes Minimales Ergebnisse", metrics);
    }

    private void displayResults(List<BacktestResultContent> data, String name, List<MetricCalculator> metricCalculators) {

        int curr = 0;
        for (BacktestResultContent result : data) {
            curr += 1;
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ {} ({} / {}) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", name, curr, data.size());
            logger.info("\t\tParameter: {}", result.parametersJson());
            logger.info("\t\tMin: {}", result.min());
            logger.info("\t\tMax: {}", result.max());
            logger.info("\t\tCum: {}", result.cumulative());

            metricCalculators.forEach(metricCalculator -> {
                Writable metric = metricCalculator.calculate(result);
                metric.getLines().forEach(line -> logger.info("\t\t{}", line));
            });
        }
    }

    private void displayBaseData(BacktestResultContent baseData) {
        LocalDateTime fromTime = baseData.fromTime();
        LocalDateTime toTime = baseData.toTime();
        long daysBetween = ChronoUnit.DAYS.between(fromTime, toTime);
        logger.info("=========================================== Basisdaten ===========================================");
        logger.info("Pairs: {}", baseData.parsePairs());
        logger.info("Testzeitraum: {} - {} ({} Tage)", fromTime.format(DateTimeFormatter.ISO_DATE_TIME), toTime.format(DateTimeFormatter.ISO_DATE_TIME), daysBetween);
        logger.info("=========================================== Strategien ===========================================");
    }

}
