package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.math.Decimal;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayOfWeekHitRate implements MetricCalculator {
    @Override
    public Writable calculate(BacktestResultContent content) {
        Map<DayOfWeek, List<Decimal>> profitsPerDay = new HashMap<>();

        content.tradeObjects().forEach(trade -> {
            DayOfWeek dayOfWeek = trade.getOpenTime().getDayOfWeek();
            profitsPerDay.computeIfAbsent(dayOfWeek, k -> new ArrayList<>());
            profitsPerDay.get(dayOfWeek).add(trade.getProfitInEuroWithFees());
        });

        List<Writable> result = new ArrayList<>();

        for (Map.Entry<DayOfWeek, List<Decimal>> dayProfit : profitsPerDay.entrySet()) {
            long profit = dayProfit.getValue().stream()
                    .filter(Decimal::isGreaterThanZero)
                    .count();

            double hitRate = profit / ((double) dayProfit.getValue().size()) * 100;

            result.add(() -> List.of("Hit Rate " + dayProfit.getKey() + ": " + hitRate + "%, Number of Trades: " + dayProfit.getValue().size()));
        }

        return new MultiMetric(result);
    }
}
