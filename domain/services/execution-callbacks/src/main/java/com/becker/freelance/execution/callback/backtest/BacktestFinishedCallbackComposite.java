package com.becker.freelance.execution.callback.backtest;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.service.ExtServiceLoader;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BacktestFinishedCallbackComposite implements BacktestFinishedCallback {

    private final Set<BacktestFinishedCallback> backtestFinishedCallbacks;

    public BacktestFinishedCallbackComposite(Set<BacktestFinishedCallback> defaultBacktestFinishedCallbacks) {
        this.backtestFinishedCallbacks = new HashSet<>(defaultBacktestFinishedCallbacks);
        this.backtestFinishedCallbacks.addAll(ExtServiceLoader.loadMultiple(BacktestFinishedCallback.class).collect(Collectors.toSet()));
    }

    @Override
    public void initiate(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, String strategyName) {
        backtestFinishedCallbacks.forEach(backtestFinishedCallback -> backtestFinishedCallback.initiate(appConfiguration, backtestExecutionConfiguration, strategyName));
    }

    @Override
    public void accept(List<Trade> trades, StrategyCreationParameter strategyCreationParameter) {
        backtestFinishedCallbacks.forEach(backtestFinishedCallback -> backtestFinishedCallback.accept(trades, strategyCreationParameter));
    }
}
