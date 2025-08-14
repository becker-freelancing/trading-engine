package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;

import java.util.List;

public interface ResultVisualizer {

    public void visualize(
            String strategyName,
            BacktestResultContent baseData,
            List<BacktestResultContent> bestCumulative,
            List<BacktestResultContent> bestMax,
            List<BacktestResultContent> bestMin,
            List<BacktestResultContent> mostTrades,
            List<MetricCalculator> metrics
    );
}
