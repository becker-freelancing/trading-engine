package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.math.Decimal;

public class ProfitHitRatioWithoutFees implements MetricCalculator {
    @Override
    public Metric calculate(BacktestResultContent content) {
        long count = content.tradeObjects().stream()
                .map(trade -> trade.getProfitInEuroWithFees().add(trade.getOpenFee()).add(trade.getCloseFee()))
                .filter(Decimal::isGreaterThanZero)
                .count();
        double ratio = count / (double) content.tradeProfits().size() * 100;
        return new Metric("Profit Hit Ratio (Without Fees)", ratio, "%");
    }
}
