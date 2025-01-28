package com.becker.freelance.backtest.resultviewer.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

class BacktestResultConsoleWriter implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(BacktestResultConsoleWriter.class);

    private final Set<BacktestResultContent> bestCumulative;
    private final Set<BacktestResultContent> bestMax;
    private final Set<BacktestResultContent> bestMin;
    private final BacktestResultContent baseData;

    public BacktestResultConsoleWriter(Set<BacktestResultContent> bestCumulative, Set<BacktestResultContent> bestMax, Set<BacktestResultContent> bestMin, BacktestResultContent baseData) {
        this.bestCumulative = bestCumulative;
        this.bestMax = bestMax;
        this.bestMin = bestMin;
        this.baseData = baseData;
    }

    @Override
    public void run() {
        displayBaseData();
        displayResults(bestCumulative, "Bestes Kumulatives Ergebnisse");
        displayResults(bestMax, "Bestes Maximales Ergebnisse");
        displayResults(bestMin, "Bestes Minimales Ergebnisse");
    }


    private void displayResults(Set<BacktestResultContent> data, String name) {

        int curr = 0;
        for(BacktestResultContent result : data) {
            curr += 1;
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ {} ({} / {}) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", name, curr, data.size());
            logger.info("\t\tParameter: {}", result.parametersJson());
            logger.info("\t\tMin: {}", result.min());
            logger.info("\t\tMax: {}", result.max());
            logger.info("\t\tCum: {}", result.cumulative());
        }
    }

    private void displayBaseData() {
        LocalDateTime fromTime = baseData.fromTime();
        LocalDateTime toTime = baseData.toTime();
        long daysBetween = ChronoUnit.DAYS.between(fromTime, toTime);
        logger.info("=========================================== Basisdaten ===========================================");
        logger.info("Pair: {}", baseData.pair());
        logger.info("Testzeitraum: {} - {} ({} Tage)", fromTime.format(DateTimeFormatter.ISO_DATE_TIME), toTime.format(DateTimeFormatter.ISO_DATE_TIME), daysBetween);
        logger.info("=========================================== Strategien ===========================================");
    }
}
