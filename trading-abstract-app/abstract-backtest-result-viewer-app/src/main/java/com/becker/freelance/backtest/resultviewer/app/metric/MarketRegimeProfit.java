package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketRegimeProfit implements MetricCalculator {
    @Override
    public Writable calculate(BacktestResultContent content) {
        List<Trade> trades = content.tradeObjects();

        Map<TradeableQuantilMarketRegime, List<Decimal>> profits = new HashMap<>();

        for (Trade trade : trades) {
            TradeableQuantilMarketRegime regime = trade.getOpenMarketRegime();
            profits.computeIfAbsent(regime, k -> new ArrayList<>());
            profits.get(regime).add(trade.getProfitInEuroWithFees());
        }

        List<Writable> result = new ArrayList<>();

        profits.forEach((regime, prof) -> {
            Decimal sum = prof.stream().reduce(Decimal::add).orElse(Decimal.ZERO);

            Decimal from = sum.divide(content.cumulative()).multiply(100);

            result.add(() -> List.of(String.format("Regime: %S, Total Profit: %s, Percentage Profit: %s", regime.name(), sum, from)));
        });

        return new MultiMetric(result);
    }
}
