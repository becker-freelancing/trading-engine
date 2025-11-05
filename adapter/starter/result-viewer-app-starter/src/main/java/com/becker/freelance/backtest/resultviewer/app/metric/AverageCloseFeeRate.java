package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

public class AverageCloseFeeRate implements MetricCalculator {
    @Override
    public Metric calculate(BacktestResultContent content) {
        double close = content.tradeObjects().stream().map(Trade::getCloseFee)
                .map(Decimal::doubleValue)
                .mapToDouble(Double::doubleValue)
                .average().orElse(0.);

        return new Metric("Average Close Fee", close, "€");
    }
}
