package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;

import java.math.BigDecimal;

public class MaxGain implements MetricCalculator {
    @Override
    public Writable calculate(BacktestResultContent content) {
        double maxGain = content.tradeProfits().stream()
                .mapToDouble(BigDecimal::doubleValue)
                .max().orElse(0.);

        return new Metric("Maximum Gain", maxGain, "€");
    }
}
