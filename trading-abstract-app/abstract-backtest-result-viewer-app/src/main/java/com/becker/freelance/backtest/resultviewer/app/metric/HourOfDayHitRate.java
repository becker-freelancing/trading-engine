package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HourOfDayHitRate implements MetricCalculator {

    @Override
    public Writable calculate(BacktestResultContent content) {
        Map<Integer, List<Decimal>> profitsPerHour = new HashMap<>();

        content.tradeObjects().forEach(trade -> {
            Integer hour = trade.getOpenTime().getHour();
            profitsPerHour.computeIfAbsent(hour, k -> new ArrayList<>());
            profitsPerHour.get(hour).add(trade.getProfitInEuroWithFees());
        });

        List<Writable> result = new ArrayList<>();

        for (Map.Entry<Integer, List<Decimal>> hourProfit : profitsPerHour.entrySet()) {
            long profit = hourProfit.getValue().stream()
                    .filter(Decimal::isGreaterThanZero)
                    .count();

            double hitRate = profit / ((double) hourProfit.getValue().size()) * 100;

            result.add(new Metric("Hit Rate Hour " + hourProfit.getKey(), hitRate, "%"));
        }

        return new MultiMetric(result);
    }
}
