package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.math.Decimal;

import java.util.List;

public class ReturnsStd implements MetricCalculator {
    @Override
    public Metric calculate(BacktestResultContent content) {
        List<Double> profits = content.tradeProfits().stream().map(Decimal::doubleValue).toList();

        Double average = profits.stream().mapToDouble(Double::doubleValue).average().orElse(0.);
        Double std = Math.sqrt(profits.stream().map(p -> p - average).map(d -> Math.pow(d, 2.)).reduce(Double::sum).orElse(0.) / profits.size());

        return new Metric("Profits Std", std, "€");
    }
}
