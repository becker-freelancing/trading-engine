package com.becker.freelance.tradeexecution;

import com.becker.freelance.commons.trade.Trade;

import java.time.LocalDateTime;
import java.util.*;

public class ClosedTradesHolder {

    private final NavigableSet<Trade> trades;

    public ClosedTradesHolder() {
        trades = new TreeSet<>();
    }

    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    public Set<Trade> getTradesInRange(LocalDateTime start, LocalDateTime end) {
        SearchTrade startSearchTrade = new SearchTrade(start);
        SearchTrade endSearchTrade = new SearchTrade(end);

        return trades.subSet(startSearchTrade, false, endSearchTrade, true);
    }

    public void addAll(List<Trade> trades) {
        trades.forEach(this::addTrade);
    }

    public List<Trade> toList() {
        return trades.stream()
                .sorted(Comparator.comparing(Trade::getCloseTime))
                .toList();
    }

    private static class SearchTrade extends Trade {

        SearchTrade(LocalDateTime start) {
            super(null, start, null, null, null, null, null, null, null, null, null, null, null);
        }
    }
}
