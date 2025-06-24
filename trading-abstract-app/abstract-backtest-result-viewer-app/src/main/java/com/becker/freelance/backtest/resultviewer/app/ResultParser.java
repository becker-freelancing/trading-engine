package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;

import java.util.List;

public interface ResultParser {

    public List<ResultExtractor> getResultExtractors();

    public void run(List<MetricCalculator> metrics, String strategyName);
}
