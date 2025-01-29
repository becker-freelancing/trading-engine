package com.becker.freelance.backtest.commons;

import com.becker.freelance.math.Decimal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record BacktestResultContent(ObjectMapper objectMapper, String pair, String appMode, LocalDateTime fromTime, LocalDateTime toTime, Decimal min, Decimal max, Decimal cumulative, Decimal initialWalletAmount, String parametersJson, String tradesJson) {

    public List<Decimal> tradeProfits() {
        JSONArray trades = new JSONArray(tradesJson());
        return IntStream.range(0, trades.length()).mapToObj(trades::getJSONObject)
                .map(trade -> trade.getBigDecimal("profitInEuro"))
                .map(Decimal::new)
                .toList();
    }

    public Map<String, Decimal> parameters(){
        return new JSONObject(parametersJson()).toMap().entrySet().stream()
                .map(entry -> {
                    Decimal decimalValue;
                    Object value = entry.getValue();
                    if (value instanceof Integer intValue){
                        decimalValue = new Decimal(intValue);
                    } else if (value instanceof BigDecimal bigDecimalValue) {
                        decimalValue = new Decimal(bigDecimalValue);
                    } else {
                        throw new IllegalStateException("Could not map " + entry.getKey() + " from instance " + value.getClass());
                    }
                    return Map.entry(entry.getKey(), decimalValue);
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

}
