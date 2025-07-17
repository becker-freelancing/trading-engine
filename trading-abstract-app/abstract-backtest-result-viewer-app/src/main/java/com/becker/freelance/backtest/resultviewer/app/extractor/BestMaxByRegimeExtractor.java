package com.becker.freelance.backtest.resultviewer.app.extractor;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.TradeStatistic;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BestMaxByRegimeExtractor implements RegimeResultExtractor {

    private Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> results = new HashMap<>();
    private Map<TradeableQuantilMarketRegime, Decimal> bestValues = new HashMap<>();

    @Override
    public Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> getResultByRegime() {
        return results;
    }

    @Override
    public void consume(BacktestResultContent resultContent) {
        Map<TradeableQuantilMarketRegime, TradeStatistic> tradeableQuantilMarketRegimeListMap = resultContent.tradeObjectsForRegime();

        for (Map.Entry<TradeableQuantilMarketRegime, TradeStatistic> entry : tradeableQuantilMarketRegimeListMap.entrySet()) {
            TradeStatistic tradeStatistic = entry.getValue();
            TradeableQuantilMarketRegime regime = entry.getKey();
            Decimal max = tradeStatistic.getMax();

            if (bestValues.getOrDefault(regime, Decimal.MINUS_DOUBLE_MAX).isLessThan(max)) {
                bestValues.put(regime, max);
                results.put(regime, new ArrayList<>());
            }

            if (bestValues.get(regime).isEqualTo(max)) {
                results.get(regime).add(new BacktestResultContent(
                        resultContent.objectMapper(),
                        resultContent.pairs(),
                        resultContent.appMode(),
                        resultContent.fromTime(),
                        resultContent.toTime(),
                        tradeStatistic.getMin(),
                        tradeStatistic.getMax(),
                        tradeStatistic.getCumulative(),
                        resultContent.initialWalletAmount(),
                        resultContent.parametersJson(),
                        tradeStatistic.getTrades()
                ));
            }
        }


    }
}
