package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Trade;
import org.json.JSONArray;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

record BacktestResultContent(String pair, LocalDateTime fromTime, LocalDateTime toTime, Double min, Double max, Double cumulative, Double initialWalletAmount, String parametersJson, String tradesJson) {

    public List<Double> tradeProfits() {
        JSONArray trades = new JSONArray(tradesJson());
        return IntStream.range(0, trades.length()).mapToObj(trades::getJSONObject)
                .map(trade -> trade.getDouble("profitInEuro"))
                .toList();
    }
}
