package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.backtest.resultviewer.app.extractor.BaseDataExtractor;
import com.becker.freelance.backtest.resultviewer.app.extractor.BestCumulativeExtractor;
import com.becker.freelance.backtest.resultviewer.app.extractor.BestMaxExtractor;
import com.becker.freelance.backtest.resultviewer.app.extractor.BestMinExtractor;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;

import java.util.List;

public class WithoutRegimeResultParser implements ResultParser {

    private final BaseDataExtractor baseDataExtractor;
    private final BestCumulativeExtractor bestCumulativeExtractor;
    private final BestMaxExtractor bestMaxExtractor;
    private final BestMinExtractor bestMinExtractor;

    public WithoutRegimeResultParser() {
        this.baseDataExtractor = new BaseDataExtractor();
        this.bestCumulativeExtractor = new BestCumulativeExtractor();
        this.bestMaxExtractor = new BestMaxExtractor();
        this.bestMinExtractor = new BestMinExtractor();
    }


    @Override
    public void run(List<MetricCalculator> metrics) {

        List<BacktestResultContent> bestCumulative = bestCumulativeExtractor.getResult();
        List<BacktestResultContent> bestMax = bestMaxExtractor.getResult();
        List<BacktestResultContent> bestMin = bestMinExtractor.getResult();
        BacktestResultContent baseData = baseDataExtractor.getResult().get(0);

        new BacktestResultConsoleWriter(bestCumulative, bestMax, bestMin, metrics, baseData).run();
        new BacktestResultPlotter(bestCumulative, bestMax, bestMin).run();
    }

    @Override
    public List<ResultExtractor> getResultExtractors() {
        return List.of(baseDataExtractor, bestCumulativeExtractor, bestMaxExtractor, bestMinExtractor);
    }
}
