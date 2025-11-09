package com.becker.freelance.trading.external.services.backtest.callbacks;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.trading.external.services.backtest.earlystop.EarlyStopCallbackResult;
import com.becker.freelance.trading.external.services.registry.ExternalService;
import com.becker.freelance.trading.external.services.strategies.StrategyCreationParameter;

import java.util.List;

public interface BacktestFinishedCallback extends ExternalService {

    public void initiate(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, String strategyName);

    void accept(Decimal executionId, List<Trade> allClosedTrades, StrategyCreationParameter parameters, EarlyStopCallbackResult earlyStopCallbackResult);

    public void onAllFinished();
}
