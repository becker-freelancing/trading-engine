package com.becker.freelance.app;

import com.becker.freelance.backtest.configuration.BacktestMode;
import com.becker.freelance.backtest.configuration.BacktestStage;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.trading.api.LocalBacktestPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class AbstractLocalBacktestAppBuilder {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLocalBacktestAppBuilder.class);

    private Decimal initialWalletAmount;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;
    private boolean continueMode;
    private Runnable onFinished = () -> {
    };
    private boolean strategyConfig;
    private String strategyName;
    private Integer numberOfThreads;
    private String appMode;
    private List<String> pair;
    private int parameterLimit;
    private String backtestMode;
    private String backtestStage;

    AbstractLocalBacktestAppBuilder() {
    }

    public static AbstractLocalBacktestAppBuilder builder() {
        return new AbstractLocalBacktestAppBuilder();
    }

    public AbstractLocalBacktestAppBuilder withStrategyName(String strategyName) {
        this.strategyName = strategyName;
        return this;
    }

    public AbstractLocalBacktestAppBuilder withNumberOfThreads(Integer numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        return this;
    }

    public AbstractLocalBacktestAppBuilder withAppMode(String appMode) {
        this.appMode = appMode;
        return this;
    }

    public AbstractLocalBacktestAppBuilder withPair(String... pairs) {
        this.pair = List.of(pairs);
        return this;
    }

    public AbstractLocalBacktestAppBuilder withBacktestMode(String backtestMode) {
        this.backtestMode = backtestMode;
        return this;
    }

    public AbstractLocalBacktestAppBuilder withBacktestStage(String backtestStage) {
        this.backtestStage = backtestStage;
        return this;
    }

    public AbstractLocalBacktestAppBuilder withInitialWalletAmount(Decimal initialWalletAmount) {
        this.initialWalletAmount = initialWalletAmount;
        return this;
    }

    public AbstractLocalBacktestAppBuilder withFromTime(LocalDateTime fromTime) {
        this.fromTime = fromTime;
        return this;
    }

    public AbstractLocalBacktestAppBuilder withToTime(LocalDateTime toTime) {
        this.toTime = toTime;
        return this;
    }

    public AbstractLocalBacktestAppBuilder continueMode() {
        this.continueMode = true;
        return this;
    }


    public AbstractLocalBacktestAppBuilder withOnFinished(Runnable runnable) {
        this.onFinished = runnable;
        return this;
    }

    public AbstractLocalBacktestAppBuilder withStrategyConfig() {
        this.strategyConfig = true;
        return this;
    }


    public LocalBacktestPort build() {

        if (continueMode) {
            throw new UnsupportedOperationException("Must be implemented");
//            return new AbstractLocalBacktestContinueApp().build();
        }

        if (initialWalletAmount == null) {
            throw new IllegalStateException("InitialWalletAmount can not be null");
        }

        if (fromTime == null) {
            throw new IllegalStateException("From time can not be null");
        }

        if (toTime == null) {
            throw new IllegalStateException("To Time can not be null");
        }

        BacktestMode mode = BacktestMode.valueOf(backtestMode);
        BacktestStage stage = BacktestStage.valueOf(backtestStage);

        logConfiguration();

        if (strategyName != null) {
            return new ConfiguredAbstractLocalBacktestApp(initialWalletAmount, fromTime, toTime, onFinished, strategyConfig, strategyName, appMode, pair, numberOfThreads, parameterLimit, mode, stage).build();
        }

        return new CliAbstractLocalBacktestApp(initialWalletAmount, fromTime, toTime, onFinished, strategyConfig, mode, stage).build();
    }

    private void logConfiguration() {
        logger.info("Initial Wallet Amount: {}", initialWalletAmount);
        logger.info("From Time: {}", fromTime);
        logger.info("To Time: {}", toTime);
        logger.info("Strategy Name: {}", strategyName);
        logger.info("Pairs: {}", pair);
        logger.info("Parameter Limit: {}", parameterLimit);
        logger.info("Backtest Mode: {}", backtestMode);
        logger.info("Backtest Stage: {}", backtestStage);
    }

    public AbstractLocalBacktestAppBuilder withParameterPermutationLimit(int limit) {
        this.parameterLimit = limit;
        return this;
    }
}
