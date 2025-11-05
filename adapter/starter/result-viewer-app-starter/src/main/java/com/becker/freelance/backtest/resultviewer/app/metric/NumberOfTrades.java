package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;

public class NumberOfTrades implements MetricCalculator {
    @Override
    public Metric calculate(BacktestResultContent content) {
        long count = content.tradeProfits().size();
        return new Metric("Number of Trades", count, "");
    }
}
