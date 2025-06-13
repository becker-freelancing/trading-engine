package com.becker.freelance.backtest.resultviewer.app.extractor;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BestCumulativeByRegimeExtractor implements RegimeResultExtractor {

    private Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> results = new HashMap<>();
    private Map<TradeableQuantilMarketRegime, Decimal> bestValues = new HashMap<>();

    @Override
    public Map<TradeableQuantilMarketRegime, List<BacktestResultContent>> getResultByRegime() {
        return results;
    }

    @Override
    public void consume(BacktestResultContent resultContent) {
//        Decimal cumulative = resultContent.cumulative();
//        if (cumulative.isGreaterThan(bestValues.get(resultContent.)))
        Map<TradeableQuantilMarketRegime, List<Trade>> tradeableQuantilMarketRegimeListMap = resultContent.tradeObjectsForRegime();
        System.out.println(true);

    }
}
