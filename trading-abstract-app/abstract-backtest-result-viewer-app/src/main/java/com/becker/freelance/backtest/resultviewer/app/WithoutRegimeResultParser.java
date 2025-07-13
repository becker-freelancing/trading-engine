package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.backtest.resultviewer.app.callback.ParsedBacktestResult;
import com.becker.freelance.backtest.resultviewer.app.callback.ParsedCallback;
import com.becker.freelance.backtest.resultviewer.app.callback.ParsedTrade;
import com.becker.freelance.backtest.resultviewer.app.extractor.*;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;
import com.becker.freelance.commons.trade.Trade;

import java.nio.file.Path;
import java.util.List;

public class WithoutRegimeResultParser implements ResultParser {

    private final BaseDataExtractor baseDataExtractor;
    private final BestCumulativeExtractor bestCumulativeExtractor;
    private final BestMaxExtractor bestMaxExtractor;
    private final BestMinExtractor bestMinExtractor;
    private final MostTradesExtractor mostTradesExtractor;

    public WithoutRegimeResultParser() {
        this.baseDataExtractor = new BaseDataExtractor();
        this.bestCumulativeExtractor = new BestCumulativeExtractor();
        this.bestMaxExtractor = new BestMaxExtractor();
        this.bestMinExtractor = new BestMinExtractor();
        this.mostTradesExtractor = new MostTradesExtractor();
    }


    @Override
    public void run(List<MetricCalculator> metrics, String strategyName, ParsedCallback parsedCallback, Path resultPath) {

        List<BacktestResultContent> bestCumulative = bestCumulativeExtractor.getResult();
        List<BacktestResultContent> bestMax = bestMaxExtractor.getResult();
        List<BacktestResultContent> bestMin = bestMinExtractor.getResult();
        BacktestResultContent baseData = baseDataExtractor.getResult().get(0);
        List<BacktestResultContent> mostTrades = mostTradesExtractor.getResult();

        new BacktestResultConsoleWriter(bestCumulative, bestMax, bestMin, metrics, baseData).run();
        new BacktestResultPlotter(strategyName, bestCumulative, bestMax, bestMin, mostTrades).run();

        parsedCallback.onBestCumulative(bestCumulative.stream().map(this::map).toList(), resultPath);
        parsedCallback.onBestMax(bestMax.stream().map(this::map).toList(), resultPath);
        parsedCallback.onBestMin(bestMin.stream().map(this::map).toList(), resultPath);
        parsedCallback.onMostTrades(mostTrades.stream().map(this::map).toList(), resultPath);
    }

    private ParsedBacktestResult map(BacktestResultContent resultContent) {
        List<ParsedTrade> parsedTrades = resultContent.tradeObjects().stream()
                .map(this::map)
                .toList();

        return new ParsedBacktestResult(parsedTrades);
    }

    private ParsedTrade map(Trade trade) {
        return new ParsedTrade(trade.getOpenTime(), trade.getProfitInEuroWithFees());
    }

    @Override
    public List<ResultExtractor> getResultExtractors() {
        return List.of(baseDataExtractor, bestCumulativeExtractor, bestMaxExtractor, bestMinExtractor, mostTradesExtractor);
    }
}
