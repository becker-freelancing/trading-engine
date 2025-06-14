package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.List;

public class TradeStatistic {

    private final List<Trade> trades;
    private Decimal min = Decimal.DOUBLE_MAX;
    private Decimal max = Decimal.MINUS_DOUBLE_MAX;
    private Decimal cumulative = Decimal.ZERO;

    public TradeStatistic() {
        this.trades = new ArrayList<>();
    }

    public void addTrade(Trade trade) {
        trades.add(trade);
        cumulative = cumulative.add(trade.getProfitInEuroWithFees());
        min = min.min(cumulative);
        max = max.max(cumulative);
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public Decimal getMin() {
        return min;
    }

    public Decimal getMax() {
        return max;
    }

    public Decimal getCumulative() {
        return cumulative;
    }
}
