package com.becker.freelance.backtest.resultviewer.app.metric;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

import java.util.List;

public class SellProfitHitRate implements MetricCalculator {
    @Override
    public Writable calculate(BacktestResultContent content) {
        List<Decimal> buyProfits = content.tradeObjects().stream()
                .filter(trade -> Direction.SELL.equals(trade.getDirection()))
                .map(Trade::getProfitInEuroWithFees)
                .toList();

        long profitable = buyProfits.stream()
                .filter(Decimal::isGreaterThanZero)
                .count();

        double hitRate = profitable / ((double) buyProfits.size()) * 100;

        return new Metric("Sell Hit Rate", hitRate, "%");
    }
}
