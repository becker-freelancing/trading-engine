package com.becker.freelance.backtest.callbacks;

import com.becker.freelance.backtest.commons.BacktestResultWriter;
import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.execution.callback.backtest.BacktestFinishedCallback;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.io.IOException;
import java.util.List;

public class BacktestResultWriterCallback implements BacktestFinishedCallback {

    private final BacktestResultWriter backtestResultWriter;

    public BacktestResultWriterCallback(BacktestResultWriter backtestResultWriter) {
        this.backtestResultWriter = backtestResultWriter;
    }

    @Override
    public void initiate(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, String strategyName) {

    }

    @Override
    public void accept(List<Trade> trades, StrategyCreationParameter strategyCreationParameter) {
        try {
            backtestResultWriter.writeResult(trades, strategyCreationParameter.asMap());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
