package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;

import java.util.List;

public interface MetricCalculator {

    public default List<Metric> calculate(List<BacktestResultContent> contents) {
        return contents.stream()
                .map(this::calculate)
                .toList();
    }

    public Metric calculate(BacktestResultContent content);
}
