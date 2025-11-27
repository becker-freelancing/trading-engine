package com.becker.freelance.backtest;

import com.becker.freelance.backtest.configuration.BacktestMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.trading.external.services.backtest.candles.BacktestDataTotalTimeProvider;
import com.becker.freelance.trading.external.services.backtest.candles.BacktestDataTotalTimeProviderBuilder;
import com.becker.freelance.trading.external.services.backtest.candles.BacktestDataTotalTimeProviderBuilderParams;
import com.becker.freelance.trading.external.services.registry.ExternalServiceRegistry;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

class BacktestModeTimeValidator implements Predicate<LocalDateTime> {

    private static final Logger logger = LoggerFactory.getLogger(BacktestModeTimeValidator.class);

    private final Duration executionDuration;
    private final Duration skipDuration;

    private LocalDateTime currentExecutionStartTime;
    private LocalDateTime currentExecutionEndTime;
    private boolean shiftedWindow = true;

    public BacktestModeTimeValidator(BacktestMode backtestMode, LocalDateTime backtestStartTime, List<Pair> pairs) {
        Duration trainDuration;
        Duration testDuration;
        Duration valDuration;
        try {
            JSONObject jsonObject = new JSONObject(new String(BacktestModeTimeValidator.class.getClassLoader().getResource("datasplit-config.json").openStream().readAllBytes()));
            trainDuration = Duration.ofMinutes(jsonObject.getLong("trainDurationInMinutes"));
            valDuration = Duration.ofMinutes(jsonObject.getLong("validationDurationInMinutes"));
            testDuration = Duration.ofMinutes(jsonObject.getLong("testDurationInMinutes"));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read datasplit-config.json", e);
        }

        Duration initialSkipDuration = getInitialSkipDuration(backtestMode, trainDuration, valDuration);
        this.executionDuration = getExecutionDuration(backtestMode, trainDuration, valDuration, testDuration);
        this.skipDuration = getSkipDuration(backtestMode, trainDuration, valDuration, testDuration);

        BacktestDataTotalTimeProvider backtestDataTotalTimeProvider = ExternalServiceRegistry.globalServiceRegistry().requireServiceBuilder(BacktestDataTotalTimeProviderBuilder.class).build(new BacktestDataTotalTimeProviderBuilderParams(backtestStartTime));

        LocalDateTime startTime = pairs.stream()
                .map(backtestDataTotalTimeProvider::getAbsoluteMinTime)
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalStateException("could not find min time for pairs " + pairs))
                .plus(initialSkipDuration);

        if (!startTime.equals(backtestStartTime)) {
            logger.warn("Setting Start time to {} instead of {} because this is the absolute min time in the database.", startTime, backtestStartTime);
        }

        this.currentExecutionStartTime = startTime.plus(initialSkipDuration);
        this.currentExecutionEndTime = currentExecutionStartTime.plus(executionDuration);

        logger.info("Current Start Time set to {} and End Time set to {}", currentExecutionStartTime, currentExecutionEndTime);
        logger.info("Initial Skip duration: {} ({})", initialSkipDuration, initialSkipDuration.toDays());
        logger.info("Execution duration: {} ({})", executionDuration, executionDuration.toDays());
        logger.info("Skip duration: {} ({})", skipDuration, skipDuration.toDays());
        logger.info("Setting first time window from {} to {}", currentExecutionStartTime, currentExecutionEndTime);
    }

    private Duration getInitialSkipDuration(BacktestMode backtestMode, Duration trainDuration, Duration valDuration) {
        return switch (backtestMode) {
            case TRAIN -> Duration.ZERO;
            case VALIDATION -> trainDuration;
            case TEST -> trainDuration.plus(valDuration);
        };
    }

    private Duration getExecutionDuration(BacktestMode backtestMode, Duration trainDuration, Duration valDuration, Duration testDuration) {
        return switch (backtestMode) {
            case TRAIN -> trainDuration;
            case VALIDATION -> valDuration;
            case TEST -> testDuration;
        };
    }

    private Duration getSkipDuration(BacktestMode backtestMode, Duration trainDuration, Duration valDuration, Duration testDuration) {
        return switch (backtestMode) {
            case TRAIN -> valDuration.plus(testDuration);
            case VALIDATION -> testDuration.plus(trainDuration);
            case TEST -> trainDuration.plus(valDuration);
        };
    }

    @Override
    public boolean test(LocalDateTime time) {
        if (!shiftedWindow && time.isAfter(currentExecutionEndTime)) {


            currentExecutionStartTime = currentExecutionEndTime.plus(skipDuration);
            currentExecutionEndTime = currentExecutionStartTime.plus(executionDuration);

            logger.info("Shifting Time Window to {} - {}", currentExecutionStartTime, currentExecutionEndTime);

            shiftedWindow = true;
        }

        if (afterOrEqual(time, currentExecutionStartTime) && time.isBefore(currentExecutionEndTime)) {
            shiftedWindow = false;
            return true;
        }

        return false;
    }

    private boolean beforeOrEqual(LocalDateTime time, LocalDateTime reference) {
        return time.isBefore(reference) || time.isEqual(reference);
    }

    private boolean afterOrEqual(LocalDateTime time, LocalDateTime reference) {
        return time.isAfter(reference) || time.isEqual(reference);
    }
}
