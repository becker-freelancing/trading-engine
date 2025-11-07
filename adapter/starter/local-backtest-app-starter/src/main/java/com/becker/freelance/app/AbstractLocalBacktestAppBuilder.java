package com.becker.freelance.app;

import com.becker.freelance.backtest.configuration.BacktestMode;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.trading.api.LocalBacktestPort;

import java.time.LocalDateTime;
import java.util.List;

public class AbstractLocalBacktestAppBuilder {


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
            return new AbstractLocalBacktestContinueApp().build();
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

        if (strategyName != null) {
            return new ConfiguredAbstractLocalBacktestApp(initialWalletAmount, fromTime, toTime, onFinished, strategyConfig, strategyName, appMode, pair, numberOfThreads, parameterLimit, mode).build();
        }

        return new CliAbstractLocalBacktestApp(initialWalletAmount, fromTime, toTime, onFinished, strategyConfig, mode).build();
    }

    public AbstractLocalBacktestAppBuilder withParameterPermutationLimit(int limit) {
        this.parameterLimit = limit;
        return this;
    }
}
