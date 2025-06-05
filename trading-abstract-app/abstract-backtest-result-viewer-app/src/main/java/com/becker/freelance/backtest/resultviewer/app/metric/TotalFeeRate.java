package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.math.Decimal;

public class TotalFeeRate implements MetricCalculator {
    @Override
    public Metric calculate(BacktestResultContent content) {
        double total = content.tradeObjects().stream().map(trade -> trade.getOpenFee().add(trade.getCloseFee()))
                .map(Decimal::doubleValue)
                .mapToDouble(Double::doubleValue)
                .sum();

        return new Metric("Total Fees", total, "€");
    }
}
