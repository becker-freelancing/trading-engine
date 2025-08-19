package com.becker.freelance.backtest.resultviewer.app.extractor;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.TradeStatistic;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BestMinByRegimeExtractor implements RegimeResultExtractor {

    private Map<TradeableMarketRegime, List<BacktestResultContent>> results = new HashMap<>();
    private Map<TradeableMarketRegime, Decimal> bestValues = new HashMap<>();

    @Override
    public Map<TradeableMarketRegime, List<BacktestResultContent>> getResultByRegime() {
        return results;
    }

    @Override
    public void consume(BacktestResultContent resultContent) {
        Map<TradeableMarketRegime, TradeStatistic> tradeableQuantilMarketRegimeListMap = resultContent.tradeObjectsForRegime();

        for (Map.Entry<TradeableMarketRegime, TradeStatistic> entry : tradeableQuantilMarketRegimeListMap.entrySet()) {
            TradeStatistic tradeStatistic = entry.getValue();
            TradeableMarketRegime regime = entry.getKey();
            Decimal min = tradeStatistic.getMin();
            if (bestValues.getOrDefault(regime, Decimal.MINUS_DOUBLE_MAX).isLessThan(min)) {
                bestValues.put(regime, min);
                results.put(regime, new ArrayList<>());
            }

            if (bestValues.get(regime).isEqualTo(min)) {
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
