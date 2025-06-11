package com.becker.freelance.tradeexecution;

import com.becker.freelance.commons.trade.Trade;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ClosedTradesHolder {

    private final NavigableMap<LocalDateTime, List<Trade>> trades;

    public ClosedTradesHolder() {
        trades = new TreeMap<>();
    }

    public void addTrade(Trade trade) {
        trades.computeIfAbsent(trade.getCloseTime(), k -> new ArrayList<>())
                .add(trade);
    }

    public Set<Trade> getTradesInRange(LocalDateTime start, LocalDateTime end) {

        return trades.subMap(start, true, end, true)
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public void addAll(List<Trade> trades) {
        trades.forEach(this::addTrade);
    }

    public List<Trade> toList() {
        return trades
                .values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }
}
