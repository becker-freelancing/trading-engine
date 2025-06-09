package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.math.Decimal;

import java.util.List;

public class AverageProfitPerTrade implements MetricCalculator {
    @Override
    public Metric calculate(BacktestResultContent content) {
        List<Decimal> profits = content.tradeProfits();
        double sum = profits.stream()
                .reduce(Decimal::add)
                .map(Decimal::doubleValue).orElse(0.);
        double ratio = sum / (double) profits.size();
        return new Metric("Average Profit", ratio, "€");
    }
}
