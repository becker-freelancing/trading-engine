package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;

import java.time.temporal.ChronoUnit;

public class AverageTradeHoldingDuration implements MetricCalculator {
    @Override
    public Metric calculate(BacktestResultContent content) {
        double duration = content.tradeObjects().stream()
                .map(trade -> ChronoUnit.MINUTES.between(trade.getOpenTime(), trade.getCloseTime()))
                .mapToLong(Long::longValue)
                .average().orElse(0.);

        return new Metric("Average Trade Duration", duration, "min");
    }
}
