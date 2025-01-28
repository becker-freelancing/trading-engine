package com.becker.freelance.backtest.commons;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record BacktestResultContent(String pair, String appMode, LocalDateTime fromTime, LocalDateTime toTime, Double min, Double max, Double cumulative, Double initialWalletAmount, String parametersJson, String tradesJson) {

    public List<Double> tradeProfits() {
        JSONArray trades = new JSONArray(tradesJson());
        return IntStream.range(0, trades.length()).mapToObj(trades::getJSONObject)
                .map(trade -> trade.getDouble("profitInEuro"))
                .toList();
    }

    public Map<String, Double> parameters(){
        return new JSONObject(parametersJson()).toMap().entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), (Double) entry.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

}
