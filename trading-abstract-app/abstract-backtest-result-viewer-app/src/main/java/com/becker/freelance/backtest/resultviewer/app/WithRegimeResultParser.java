package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.backtest.resultviewer.app.callback.ParsedBacktestResult;
import com.becker.freelance.backtest.resultviewer.app.callback.ParsedCallback;
import com.becker.freelance.backtest.resultviewer.app.callback.ParsedTrade;
import com.becker.freelance.backtest.resultviewer.app.extractor.*;
import com.becker.freelance.backtest.resultviewer.app.metric.MaxDrawdownMetric;
import com.becker.freelance.backtest.resultviewer.app.metric.Metric;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;
import com.becker.freelance.backtest.resultviewer.app.metric.ProfitHitRatio;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class WithRegimeResultParser implements ResultParser {

    private static final Logger logger = LoggerFactory.getLogger(WithRegimeResultParser.class);

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

        printAdditionalInfos();
        parsedCallback.onBestCumulative(bestCumulative.stream().map(this::map).toList(), resultPath);
        parsedCallback.onBestMax(bestMax.stream().map(this::map).toList(), resultPath);
        parsedCallback.onBestMin(bestMin.stream().map(this::map).toList(), resultPath);
        parsedCallback.onMostTrades(mostTrades.stream().map(this::map).toList(), resultPath);
    }

    private void printAdditionalInfos() {
        if (true) {
            logger.warn("ADDITIONAL STATISTICS FOR BEST CUMULATIVES BY REGIME (ONLY FIRST ONE)");

            Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> resultByRegime = bestCumulativeExtractor.getResultByRegime();
            toCsv(resultByRegime);
            MaxDrawdownMetric maxDrawdownMetric = new MaxDrawdownMetric();
            ProfitHitRatio profitHitRatio = new ProfitHitRatio();
            for (TradeableQuantilMarketRegime regime : resultByRegime.keySet().stream().sorted(Comparator.comparing(TradeableQuantilMarketRegime::name)).toList()) {

                List<BacktestResultContent> resultContents = resultByRegime.get(regime);
                if (resultContents == null || resultContents.isEmpty()) {
                    logger.warn("Skipping regime {}", regime);
                    continue;
                }
                BacktestResultContent backtestResultContent = resultContents.get(0);
                List<Decimal> profits = backtestResultContent.tradeProfits();
                List<Double> balances = toBalances(profits);

                logger.info("regime: {}", regime);
                logger.info("\tNumber of Trades: {}", backtestResultContent.tradeObjects().size());
                logger.info("\tMax Gain: {}", profits.stream().mapToDouble(Decimal::doubleValue).max().orElse(0.));
                logger.info("\tMax Loss: {}", profits.stream().mapToDouble(Decimal::doubleValue).min().orElse(0.));
                logger.info("\tProfit Std: {}", std(profits));
                logger.info("\tMin Equity: {}", balances.stream().mapToDouble(Double::doubleValue).min().orElse(0.));
                logger.info("\tMax Equity: {}", balances.stream().mapToDouble(Double::doubleValue).max().orElse(0.));
                logger.info("\tLast Equity: {}", balances.get(balances.size() - 1));
                logger.info("\tMax Drawdown: {}", maxDrawdownMetric.calculate(backtestResultContent).value());
                logger.info("\tWinRate: {}", profitHitRatio.calculate(backtestResultContent).value());

            }
        }
    }

    private List<Double> toBalances(List<Decimal> profits) {
        List<Double> balances = new ArrayList<>();
        balances.add(0.);
        for (int i = 0; i < profits.size(); i++) {
            balances.add(balances.get(i) + profits.get(i).doubleValue());
        }
        return balances;
    }

    private void toCsv(Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> resultByRegime) {

        MaxDrawdownMetric maxDrawdownMetric = new MaxDrawdownMetric();
        ProfitHitRatio profitHitRatio = new ProfitHitRatio();

        for (String r : List.of("DOWN", "UP", "SIDE")) {
            List<BacktestResultContent> d = new ArrayList<>();
            for (String v : List.of("LOW", "HIGH")) {
                for (String q : List.of("033", "066", "1")) {
                    for (TradeableQuantilMarketRegime key : resultByRegime.keySet()) {
                        if (key.name().contains(r) && key.name().contains(v) && key.name().contains(q)) {
                            if (!resultByRegime.get(key).isEmpty())
                                d.add(resultByRegime.get(key).get(0));
                        }
                    }
                }
            }

            StringBuilder builder = new StringBuilder();
            d.stream().map(BacktestResultContent::tradeObjects).filter(l -> !l.isEmpty()).map(l -> l.get(0)).map(Trade::getOpenMarketRegime).map(TradeableQuantilMarketRegime::name).forEach(s -> builder.append(s).append(";"));
            builder.append("\n");
            d.stream().map(BacktestResultContent::tradeObjects).map(List::size).forEach(s -> builder.append(s).append(";"));
            builder.append("\n");
            d.stream().map(BacktestResultContent::tradeProfits).map(l -> l.stream().mapToDouble(Decimal::doubleValue).max().orElse(0.)).mapToInt(Double::intValue).forEach(s -> builder.append(s).append(";"));
            builder.append("\n");
            d.stream().map(BacktestResultContent::tradeProfits).map(l -> l.stream().mapToDouble(Decimal::doubleValue).min().orElse(0.)).mapToInt(Double::intValue).forEach(s -> builder.append(s).append(";"));
            builder.append("\n");
            d.stream().map(BacktestResultContent::tradeProfits).map(this::std).mapToInt(Double::intValue).forEach(s -> builder.append(s).append(";"));
            builder.append("\n");
            d.stream().map(BacktestResultContent::tradeProfits).map(this::toBalances).map(l -> l.stream().mapToDouble(Double::doubleValue).min().orElse(0.)).mapToInt(Double::intValue).forEach(s -> builder.append(s).append(";"));
            builder.append("\n");
            d.stream().map(BacktestResultContent::tradeProfits).map(this::toBalances).map(l -> l.stream().mapToDouble(Double::doubleValue).max().orElse(0.)).mapToInt(Double::intValue).forEach(s -> builder.append(s).append(";"));
            builder.append("\n");
            d.stream().map(BacktestResultContent::tradeProfits).map(this::toBalances).map(l -> l.get(l.size() - 1)).mapToInt(Double::intValue).forEach(s -> builder.append(s).append(";"));
            builder.append("\n");
            d.stream().map(maxDrawdownMetric::calculate).map(Metric::value).map(n -> (Double) n).mapToInt(Double::intValue).forEach(s -> builder.append(s).append(";"));
            builder.append("\n");
            d.stream().map(profitHitRatio::calculate).map(Metric::value).map(n -> (Double) n).mapToInt(Double::intValue).forEach(s -> builder.append(s).append(";"));
            builder.append("\n");

            Path path = Path.of("C:\\Users\\jasb\\Downloads", r + ".csv");
            try {
                if (Files.exists(path)) {
                    Files.delete(path);
                }
                Files.createFile(path);
                Files.writeString(path, builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private Double std(List<Decimal> decimals) {

        double mean = decimals.stream()
                .mapToDouble(Decimal::doubleValue)
                .average()
                .orElse(0.0);

        double variance = decimals.stream()
                .mapToDouble(d -> Math.pow(d.doubleValue() - mean, 2))
                .average()
                .orElse(0.0);

        double stdDeviation = Math.sqrt(variance);
        return stdDeviation;
    }

    private ParsedBacktestResult map(BacktestResultContent resultContent) {
        List<ParsedTrade> parsedTrades = resultContent.tradeObjects().stream()
                .map(this::map)
                .toList();

        return new ParsedBacktestResult(parsedTrades, resultContent);
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
