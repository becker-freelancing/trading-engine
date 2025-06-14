package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public final class BacktestResultContent {
    private final ObjectMapper objectMapper;
    private final String pairs;
    private final String appMode;
    private final LocalDateTime fromTime;
    private final LocalDateTime toTime;
    private final Decimal min;
    private final Decimal max;
    private final Decimal cumulative;
    private final Decimal initialWalletAmount;
    private final String parametersJson;
    private String tradesJson;
    private List<Trade> trades = null;
    private Map<TradeableQuantilMarketRegime, TradeStatistic> tradesByRegime;

    public BacktestResultContent(ObjectMapper objectMapper, String pairs, String appMode, LocalDateTime fromTime,
                                 LocalDateTime toTime, Decimal min, Decimal max, Decimal cumulative,
                                 Decimal initialWalletAmount, String parametersJson, String tradesJson) {
        this.objectMapper = objectMapper;
        this.pairs = pairs;
        this.appMode = appMode;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.min = min;
        this.max = max;
        this.cumulative = cumulative;
        this.initialWalletAmount = initialWalletAmount;
        this.parametersJson = parametersJson;
        this.tradesJson = tradesJson;
    }

    public BacktestResultContent(ObjectMapper objectMapper, String pairs, String appMode, LocalDateTime fromTime,
                                 LocalDateTime toTime, Decimal min, Decimal max, Decimal cumulative,
                                 Decimal initialWalletAmount, String parametersJson, List<Trade> trades) {
        this.objectMapper = objectMapper;
        this.pairs = pairs;
        this.appMode = appMode;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.min = min;
        this.max = max;
        this.cumulative = cumulative;
        this.initialWalletAmount = initialWalletAmount;
        this.parametersJson = parametersJson;
        this.trades = trades.stream().sorted(Comparator.comparing(Trade::getOpenTime)).toList();
    }

    public Map<TradeableQuantilMarketRegime, TradeStatistic> tradeObjectsForRegime() {
        if (tradesByRegime == null) {
            tradesByRegime = new HashMap<>();
            for (Trade trade : tradeObjects()) {
                TradeableQuantilMarketRegime tradeRegime = trade.getOpenMarketRegime();
                tradesByRegime.computeIfAbsent(tradeRegime, k -> new TradeStatistic());
                tradesByRegime.get(tradeRegime).addTrade(trade);
            }
        }

        return tradesByRegime;
    }

    public List<Trade> tradeObjects() {
        if (trades == null) {
            try {
                trades = objectMapper.readValue(tradesJson(), new TypeReference<List<Trade>>() {
                });
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Could not parse Trades", e);
            }
        }
        return trades;
    }

    public List<Decimal> tradeProfits() {
        return tradeObjects().stream()
                .map(Trade::getProfitInEuroWithFees)
                .toList();
    }

    public Map<String, Decimal> parameters() {
        return new JSONObject(parametersJson()).toMap().entrySet().stream()
                .map(entry -> {
                    Decimal decimalValue;
                    Object value = entry.getValue();
                    if (value instanceof Integer intValue) {
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

    public List<Pair> parsePairs() {
        try {
            return objectMapper.readValue(pairs, new TypeReference<List<Pair>>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not parse Pairs", e);
        }
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    public String pairs() {
        return pairs;
    }

    public String appMode() {
        return appMode;
    }

    public LocalDateTime fromTime() {
        return fromTime;
    }

    public LocalDateTime toTime() {
        return toTime;
    }

    public Decimal min() {
        return min;
    }

    public Decimal max() {
        return max;
    }

    public Decimal cumulative() {
        return cumulative;
    }

    public Decimal initialWalletAmount() {
        return initialWalletAmount;
    }

    public String parametersJson() {
        return parametersJson;
    }

    public String tradesJson() {
        return tradesJson;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BacktestResultContent) obj;
        return Objects.equals(this.objectMapper, that.objectMapper) &&
                Objects.equals(this.pairs, that.pairs) &&
                Objects.equals(this.appMode, that.appMode) &&
                Objects.equals(this.fromTime, that.fromTime) &&
                Objects.equals(this.toTime, that.toTime) &&
                Objects.equals(this.min, that.min) &&
                Objects.equals(this.max, that.max) &&
                Objects.equals(this.cumulative, that.cumulative) &&
                Objects.equals(this.initialWalletAmount, that.initialWalletAmount) &&
                Objects.equals(this.parametersJson, that.parametersJson) &&
                Objects.equals(this.tradesJson, that.tradesJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectMapper, pairs, appMode, fromTime, toTime, min, max, cumulative, initialWalletAmount, parametersJson, tradesJson);
    }

    @Override
    public String toString() {
        return "BacktestResultContent[" +
                "objectMapper=" + objectMapper + ", " +
                "pairs=" + pairs + ", " +
                "appMode=" + appMode + ", " +
                "fromTime=" + fromTime + ", " +
                "toTime=" + toTime + ", " +
                "min=" + min + ", " +
                "max=" + max + ", " +
                "cumulative=" + cumulative + ", " +
                "initialWalletAmount=" + initialWalletAmount + ", " +
                "parametersJson=" + parametersJson + ", " +
                "tradesJson=" + tradesJson + ']';
    }


}
