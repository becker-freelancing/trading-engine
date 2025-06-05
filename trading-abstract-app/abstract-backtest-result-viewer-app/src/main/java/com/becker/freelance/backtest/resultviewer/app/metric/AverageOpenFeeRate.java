package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

public class AverageOpenFeeRate implements MetricCalculator {
    @Override
    public Metric calculate(BacktestResultContent content) {
        double openFees = content.tradeObjects().stream().map(Trade::getOpenFee)
                .map(Decimal::doubleValue)
                .mapToDouble(Double::doubleValue)
                .average().orElse(0.);

        return new Metric("Average Open Fee", openFees, "€");
    }
}
