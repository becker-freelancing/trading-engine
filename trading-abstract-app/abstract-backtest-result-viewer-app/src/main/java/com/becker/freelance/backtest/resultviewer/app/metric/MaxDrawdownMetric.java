package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MaxDrawdownMetric implements MetricCalculator {
    @Override
    public Metric calculate(BacktestResultContent content) {
        List<Trade> trades = content.tradeObjects();
        Decimal accountBalance = Decimal.ZERO;
        Decimal[] accountBalancesAfterTrades = new Decimal[trades.size() + 1];
        accountBalancesAfterTrades[accountBalancesAfterTrades.length - 1] = accountBalance;
        for (int i = trades.size() - 1; i >= 0; i--) {
            accountBalance = accountBalance.subtract(trades.get(i).getProfitInEuroWithFees());
            accountBalancesAfterTrades[i] = accountBalance;
        }
        Decimal maxAccountBalance = Arrays.stream(accountBalancesAfterTrades).max(Comparator.naturalOrder()).orElse(accountBalance);
        Decimal actualMaxDrawdown = Arrays.stream(accountBalancesAfterTrades)
                .map(accountBalancesAfterTrade -> calcDrawDown(accountBalancesAfterTrade, maxAccountBalance))
                .max(Comparator.naturalOrder())
                .orElse(Decimal.ZERO);

        return new Metric("Max Drawdown", actualMaxDrawdown.doubleValue(), "%");
    }

    private Decimal calcDrawDown(Decimal accountBalancesAfterTrade, Decimal maxAccountBalance) {
        Decimal diff = maxAccountBalance.subtract(accountBalancesAfterTrade);
        return diff.divide(maxAccountBalance).multiply(Decimal.valueOf(100));
    }
}
