package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.backtest.resultviewer.app.callback.ParsedBacktestResult;
import com.becker.freelance.backtest.resultviewer.app.callback.ParsedCallback;
import com.becker.freelance.backtest.resultviewer.app.callback.ParsedTrade;
import com.becker.freelance.backtest.resultviewer.app.extractor.*;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class WithRegimeResultParser implements ResultParser {

    private final BaseDataExtractor baseDataExtractor;
    private final BestCumulativeByRegimeExtractor bestCumulativeExtractor;
    private final BestMaxByRegimeExtractor bestMaxExtractor;
    private final BestMinByRegimeExtractor bestMinExtractor;
    private final MostTradesByRegimeExtractor mostTradesExtractor;

    public WithRegimeResultParser() {
        this.baseDataExtractor = new BaseDataExtractor();
        this.bestCumulativeExtractor = new BestCumulativeByRegimeExtractor();
        this.bestMaxExtractor = new BestMaxByRegimeExtractor();
        this.bestMinExtractor = new BestMinByRegimeExtractor();
        this.mostTradesExtractor = new MostTradesByRegimeExtractor();
    }

    private static List<BacktestResultContent> permutate(Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> resultByRegime) {
        List<BacktestResultContent> results = new ArrayList<>();

        for (Map.Entry<TradeableQuantilMarketRegime, List<BacktestResultContent>> entry : resultByRegime.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().name()))
                .toList()) {
            TradeableQuantilMarketRegime regime = entry.getKey();
            List<BacktestResultContent> resultContents = entry.getValue();

            List<BacktestResultContent> newResults = new ArrayList<>();
            for (BacktestResultContent result : results) {
                for (BacktestResultContent resultContent : resultContents) {
                    List<Trade> trades = new ArrayList<>(result.tradeObjects());
                    trades.addAll(resultContent.tradeObjects());
                    trades = trades.stream().sorted(Comparator.comparing(Trade::getOpenTime)).toList();
                    List<Decimal> cumulate = cumulative(trades);
                    newResults.add(new BacktestResultContent(
                                    result.objectMapper(),
                                    result.pairs(),
                                    result.appMode(),
                                    result.fromTime(),
                                    result.toTime(),
                                    cumulate.stream().min(Comparator.naturalOrder()).orElse(Decimal.ZERO),
                                    cumulate.stream().max(Comparator.naturalOrder()).orElse(Decimal.ZERO),
                                    cumulate.get(cumulate.size() - 1),
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

    private static List<Decimal> cumulative(List<Trade> trades) {
        List<Decimal> cumulative = new ArrayList<>();
        for (int i = 0; i < trades.size(); i++) {
            if (i == 0){
                cumulative.add(trades.get(i).getProfitInEuroWithFees());
                continue;
            }

            cumulative.add(cumulative.get(i-1).add(trades.get(i).getProfitInEuroWithFees()));
        }
        return cumulative;
    }

    @Override
    public List<ResultExtractor> getResultExtractors() {
        return List.of(baseDataExtractor, bestCumulativeExtractor, bestMaxExtractor, bestMinExtractor, mostTradesExtractor);
    }

    @Override
    public void run(List<MetricCalculator> metrics, String strategyName, ParsedCallback parsedCallback, Path resultPath) {
        List<BacktestResultContent> bestMin = findBestMin();
        List<BacktestResultContent> bestMax = findBestMax();
        List<BacktestResultContent> bestCumulative = findBestCumulative();
        List<BacktestResultContent> mostTrades = findMostTrades();
        BacktestResultContent baseData = baseDataExtractor.getResult().get(0);

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

    private List<BacktestResultContent> findMostTrades() {
        Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> resultByRegime = mostTradesExtractor.getResultByRegime();

        return List.of();//permutate(resultByRegime);
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
