package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.backtest.resultviewer.app.extractor.BaseDataExtractor;
import com.becker.freelance.backtest.resultviewer.app.extractor.BestCumulativeByRegimeExtractor;
import com.becker.freelance.backtest.resultviewer.app.extractor.BestMaxByRegimeExtractor;
import com.becker.freelance.backtest.resultviewer.app.extractor.BestMinByRegimeExtractor;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.trade.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WithRegimeResultParser implements ResultParser {

    private final BaseDataExtractor baseDataExtractor;
    private final BestCumulativeByRegimeExtractor bestCumulativeExtractor;
    private final BestMaxByRegimeExtractor bestMaxExtractor;
    private final BestMinByRegimeExtractor bestMinExtractor;

    public WithRegimeResultParser() {
        this.baseDataExtractor = new BaseDataExtractor();
        this.bestCumulativeExtractor = new BestCumulativeByRegimeExtractor();
        this.bestMaxExtractor = new BestMaxByRegimeExtractor();
        this.bestMinExtractor = new BestMinByRegimeExtractor();
    }

    private static List<BacktestResultContent> permutate(Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> resultByRegime) {
        List<BacktestResultContent> results = new ArrayList<>();

        for (Map.Entry<TradeableQuantilMarketRegime, List<BacktestResultContent>> entry : resultByRegime.entrySet()) {
            TradeableQuantilMarketRegime regime = entry.getKey();
            List<BacktestResultContent> resultContents = entry.getValue();

            List<BacktestResultContent> newResults = new ArrayList<>();
            for (BacktestResultContent result : results) {
                for (BacktestResultContent resultContent : resultContents) {
                    ArrayList<Trade> trades = new ArrayList<>(result.tradeObjects());
                    trades.addAll(resultContent.tradeObjects());
                    newResults.add(new BacktestResultContent(
                                    result.objectMapper(),
                                    result.pairs(),
                                    result.appMode(),
                                    result.fromTime(),
                                    result.toTime(),
                                    resultContent.min().min(result.min()),
                                    resultContent.max().max(result.max()),
                                    resultContent.cumulative().add(result.cumulative()),
                                    result.initialWalletAmount(),
                                    result.parametersJson() + "\n" + regime.name() + ": " + resultContent.parametersJson(),
                                    trades
                            )
                    );
                }

            }
            if (results.isEmpty()) {
                for (BacktestResultContent resultContent : resultContents) {
                    newResults.add(new BacktestResultContent(
                                    resultContent.objectMapper(),
                                    resultContent.pairs(),
                                    resultContent.appMode(),
                                    resultContent.fromTime(),
                                    resultContent.toTime(),
                                    resultContent.min(),
                                    resultContent.max(),
                                    resultContent.cumulative(),
                                    resultContent.initialWalletAmount(),
                            "\n" + regime.name() + ": " + resultContent.parametersJson(),
                                    resultContent.tradeObjects()
                            )
                    );
                }
            }
            results = newResults;
        }

        return results;
    }

    @Override
    public List<ResultExtractor> getResultExtractors() {
        return List.of(baseDataExtractor, bestCumulativeExtractor, bestMaxExtractor, bestMinExtractor);
    }

    @Override
    public void run(List<MetricCalculator> metrics) {
        List<BacktestResultContent> bestMin = findBestMin();
        List<BacktestResultContent> bestMax = findBestMax();
        List<BacktestResultContent> bestCumulative = findBestCumulative();
        BacktestResultContent baseData = baseDataExtractor.getResult().get(0);

        new BacktestResultConsoleWriter(bestCumulative, bestMax, bestMin, metrics, baseData).run();
        new BacktestResultPlotter(bestCumulative, bestMax, bestMin).run();
    }

    private List<BacktestResultContent> findBestMin() {
        Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> resultByRegime = bestMinExtractor.getResultByRegime();

        return permutate(resultByRegime);
    }

    private List<BacktestResultContent> findBestMax() {
        Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> resultByRegime = bestMaxExtractor.getResultByRegime();

        return permutate(resultByRegime);
    }

    private List<BacktestResultContent> findBestCumulative() {
        Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> resultByRegime = bestCumulativeExtractor.getResultByRegime();

        return permutate(resultByRegime);
    }
}
