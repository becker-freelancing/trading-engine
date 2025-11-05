package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

import java.util.List;

public class MaxDrawdownMetric implements MetricCalculator {
    @Override
    public Metric calculate(BacktestResultContent content) {
        List<Trade> trades = content.tradeObjects();
        Decimal accountBalance = Decimal.ZERO;
        Decimal[] accountBalancesAfterTrades = new Decimal[trades.size() + 1];
        accountBalancesAfterTrades[0] = accountBalance;

        for (int i = 0; i < trades.size(); i++) {
            accountBalance = accountBalance.add(trades.get(i).getProfitInEuroWithFees());
            accountBalancesAfterTrades[i + 1] = accountBalance;
        }

        Decimal peak = Decimal.ZERO;
        Decimal maxDrawdown = Decimal.ZERO;

        for (Decimal balance : accountBalancesAfterTrades) {
            if (balance.compareTo(peak) > 0) {
                peak = balance;
            }
            if (peak.compareTo(Decimal.ZERO) > 0) {
                Decimal drawdown = peak.subtract(balance).divide(peak);
                if (drawdown.compareTo(maxDrawdown) > 0) {
                    maxDrawdown = drawdown;
                }
            }
        }

        return new Metric("Max Drawdown", maxDrawdown.doubleValue() * 100, "%");
    }
}
