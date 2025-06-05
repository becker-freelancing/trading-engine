package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.resultviewer.app.metric.Metric;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

class BacktestResultConsoleWriter implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(BacktestResultConsoleWriter.class);

    private final List<BacktestResultContent> bestCumulative;
    private final List<BacktestResultContent> bestMax;
    private final List<BacktestResultContent> bestMin;
    private final List<MetricCalculator> metricCalculators;
    private final BacktestResultContent baseData;

    public BacktestResultConsoleWriter(List<BacktestResultContent> bestCumulative, List<BacktestResultContent> bestMax, List<BacktestResultContent> bestMin, List<MetricCalculator> metricCalculators, BacktestResultContent baseData) {
        this.bestCumulative = bestCumulative;
        this.bestMax = bestMax;
        this.bestMin = bestMin;
        this.metricCalculators = metricCalculators;
        this.baseData = baseData;
    }

    @Override
    public void run() {
        displayBaseData();
        displayResults(bestCumulative, "Bestes Kumulatives Ergebnisse");
        displayResults(bestMax, "Bestes Maximales Ergebnisse");
        displayResults(bestMin, "Bestes Minimales Ergebnisse");
    }


    private void displayResults(List<BacktestResultContent> data, String name) {

        int curr = 0;
        for(BacktestResultContent result : data) {
            curr += 1;
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ {} ({} / {}) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", name, curr, data.size());
            logger.info("\t\tParameter: {}", result.parametersJson());
            logger.info("\t\tMin: {}", result.min());
            logger.info("\t\tMax: {}", result.max());
            logger.info("\t\tCum: {}", result.cumulative());

            metricCalculators.forEach(metricCalculator -> {
                Metric metric = metricCalculator.calculate(result);
                logger.info("\t\t{}: {} {}", metric.name(), metric.value(), metric.unit());
            });
        }
    }

    private void displayBaseData() {
        LocalDateTime fromTime = baseData.fromTime();
        LocalDateTime toTime = baseData.toTime();
        long daysBetween = ChronoUnit.DAYS.between(fromTime, toTime);
        logger.info("=========================================== Basisdaten ===========================================");
        logger.info("Pairs: {}", baseData.parsePairs());
        logger.info("Testzeitraum: {} - {} ({} Tage)", fromTime.format(DateTimeFormatter.ISO_DATE_TIME), toTime.format(DateTimeFormatter.ISO_DATE_TIME), daysBetween);
        logger.info("=========================================== Strategien ===========================================");
    }
}
