package com.becker.freelance.execution.callback.backtest;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.util.List;
import java.util.function.BiConsumer;

public interface BacktestFinishedCallback extends BiConsumer<List<Trade>, StrategyCreationParameter> {

    public void initiate(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, String strategyName);
}
